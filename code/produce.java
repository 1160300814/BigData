package marisuki;
/*产生文件*/

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class produce {

    public String prod(String FileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(FileName));
        FileOutputStream outSTr = new FileOutputStream(new File("./sogou.500w/test.seq"));
        BufferedOutputStream Buff = new BufferedOutputStream(outSTr);
        String line = "";
        line = reader.readLine();
        int i = 0;
        while(!line.equals("")) {
            i++;
            Buff.write(line.getBytes());
            /*加符号*/
            Buff.write(";".getBytes());

            if(i%1713==0)
                Buff.write("\n".getBytes());
            line = reader.readLine();
            if(line==null || i==4000) break;

        }
        Buff.write("\n".getBytes());
        Buff.flush();
        Buff.close();
        System.out.println("-----Matrix Finish !-----");
        return "./sogou.500w/test.seq";
    }

}
