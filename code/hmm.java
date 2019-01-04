package marisuki;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationReal;
import be.ac.ulg.montefiore.run.jahmm.OpdfGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationRealReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationSequencesReader;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;

public class hmm {
    public void Hmm(String FileName) throws Exception{
        //Reader reader = new FileReader("src\\"+FileName);
        BufferedReader reader = new BufferedReader(new FileReader(FileName));
        List<List<ObservationReal>> seqs = ObservationSequencesReader.readSequences(new ObservationRealReader(),reader);
        reader.close ();
        System.out.println("-----KML Start !-----");
        KMeansLearner <ObservationReal > kml = new KMeansLearner <ObservationReal >(3, new OpdfGaussianFactory() , seqs);
        Hmm <ObservationReal> fittedHmm = kml.learn();
        System.out.println(fittedHmm);
        System.out.println("-----KML finish !-----");
        Hmm <ObservationReal> initHmm =  kml.iterate();
        KullbackLeiblerDistanceCalculator klc = new KullbackLeiblerDistanceCalculator();
        BaumWelchLearner bwl = new BaumWelchLearner();
        Hmm<ObservationReal> learntHmm = null;
        for (int i = 0; i < 4; i++) {
            learntHmm = bwl.iterate(initHmm, seqs);
            // System.out.println("Distance at iteration : " + klc.distance(learntHmm, initHmm));
        }
        System.out.println(learntHmm);
        int[]a=learntHmm.mostLikelyStateSequence(seqs.get(0));
        int lasti = -1;
        for(int i=0;i<a.length;i++) {
            if(lasti != a[i]) {
                System.out.println(i+" : "+a[i]);
                lasti = a[i];
            }
        }
        System.out.println("lnProbability:");
        System.out.println(learntHmm.lnProbability(seqs.get(0)));
        System.out.println("Probability:");
        System.out.println(learntHmm.probability(seqs.get(0)));
        (new GenericHmmDrawerDot()).write(learntHmm, "learntHmm.dot");
    }
}
