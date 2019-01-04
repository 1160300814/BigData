package marisuki

import org.apache.spark.{SparkConf, SparkContext}
import java.io.StringReader
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import au.com.bytecode.opencsv.CSVReader
import breeze.linalg._
import breeze.numerics._
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionTrainingSummary}
import java.io._

object App {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("homework")
    val sc = new SparkContext(conf)
    val File = "./sogou.500w/test.txt"
    val input = sc.textFile(File)

    val list_rdd=input.flatMap{x => x.split("\n")}
    val array_data=list_rdd.collect()
    //    val matrix_data=new DenseMatrix(4,3,array_data).t
    //    println("matrix_dada:\n"+matrix_data)

    val vector_data =new DenseVector(array_data)
    //println("vector_data : "+vector_data)

    val createMatrix= DenseMatrix.tabulate(3, 80) {
      case (i ,j) =>
        vector_data.valueAt(i+j+1).toDouble-vector_data.valueAt(j).toDouble
    }
    // println("createMatrix:\n"+createMatrix)


    val spark = SparkSession  //creat
      .builder
      .appName("NewLinearRegression")
      .master("local")
      .getOrCreate()
    import spark.implicits._
    import org.apache.spark.ml.linalg.Vectors
    import org.apache.spark.sql.Row

    //    val one_row=createMatrix.toArray
    //    println(one_row)
    //output :address

    var RMSE_list=List[Double]()

    for(i <- 0 until createMatrix.rows){
      val currentVector=createMatrix(i,::)
      val training=(0 until createMatrix.cols).map(n =>
        (n,currentVector.apply(n))).map{
        case (x,y)=>
          LabeledPoint(y,Vectors.dense(x+1))
      }.toDF("label", "features")
      //training.show()
      val lr = new LinearRegression().setMaxIter(1000).setRegParam(0.3).setElasticNetParam(0.8)
      val lrModel = lr.fit(training)  //Calculation model
      val trainingSummary = lrModel.summary
      RMSE_list=trainingSummary.rootMeanSquaredError::RMSE_list
      println(i+".RMSE:\t"+trainingSummary.rootMeanSquaredError)
    }

    RMSE_list=RMSE_list.reverse
    val new_RMSE_list=RMSE_list drop 2
    val min_RMSE_index=new_RMSE_list.indexOf(new_RMSE_list.min)+3
    println("min_RMSE_index:"+min_RMSE_index)

    val Product = new produce
    val HmmTest = new hmm
    HmmTest.Hmm(Product.prod(File))
  /*
    //val matrix_data=new DenseMatrix(min_RMSE_index,array_data.length/min_RMSE_index,array_data).t
    val allinput = new DenseVector(array_data)
    // println("matrix_dada:\n"+matrix_data)
    val file_path = "./sogou.500w/alltest.seq"
    val writer = new PrintWriter(new File(file_path))
    val matrix_data_each = array_data.mkString(";")

    writer.write(str=";")
    writer.write(str=";")
    writer.write(str="\n")*/
    // user visit frequency count
    // sogou.map(_.split("\t")).map(line => (line(1), 1)).reduceByKey(_+_).map(x => (x._2, x._1)).sortByKey(false).map(x => (x._2, x._1)).saveAsTextFile("./data/usersearchingfrequency.utf8")

    // time behavior analysis
    // sogou.map(_.split("\t")(0).substring(0, 10)).map(x => (x, 1)).reduceByKey(_+_).map(x => (x._2, x._1)).sortByKey(false).map(x => (x._2, x._1)).saveAsTextFile("./data/
    sc.stop()
  }
}
