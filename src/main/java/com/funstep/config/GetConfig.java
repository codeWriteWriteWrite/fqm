package com.funstep.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetConfig {

	public static<T> T getConfig(Class<T> clazz) throws IOException, IllegalAccessException, InstantiationException {
        return getConfig( clazz,false);
    }

    public static<T> T getConfig(Class<T> clazz,boolean createNewFile) throws IOException, IllegalAccessException, InstantiationException {
        String fileName=clazz.getSimpleName();
        XStream xStream = new XStream(new DomDriver());
        String root=System.getProperty("user.dir");
        String path=root+"/"+fileName+".xml";
        File file=new File(path);
        if(createNewFile){
            file.delete();
        }
        if(!file.exists()){
            T t=clazz.newInstance();
            file.createNewFile();
            String config = xStream.toXML(t);
            FileWriter fw=new FileWriter(file);
            fw.write(config);
            fw.flush();
            fw.close();
            return t;
        }else{
            StringBuilder builder=new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line=null;
            while ((line=br.readLine())!=null){
                builder.append(line);
            }
            br.close();
            T t = (T) xStream.fromXML(builder.toString());
            return t;
        }

    }

}
