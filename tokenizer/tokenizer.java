
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

public class tokenizer{

   public static void main(String[] args) throws Exception {
   if (args.length > 0) {
       String name = args[0];
       File file = new File(args[0]);
       
       if(file.isDirectory()){
           //a "T.xml" file will never be read from directory, just one long T.xml file
           File[] listOfFiles = file.listFiles();
           Writer writer = new Writer(name + "T.xml");
           writer.append("<tokens>" + "\n");
           for(File files : listOfFiles){
               String fileName = files.getName();
           if(fileName.contains(".jack")){
                  Parser parser = new Parser(files);
                  writer.append("//" + fileName);
                  while (parser.hasMoreCommands()) {
                     String line = parser.advanceIgnoreComments();
                  
                     writer.append(writer.toXML(line, fileName));
                
                  }
            
           }
               //writer.append("\n");
            }
            
       }
        
       else{
           Parser parser = new Parser(name);

       if(name.contains(".jack")){
              Writer writer = new Writer(name.replace(".jack","T.xml"));
              writer.append("<tokens>" + "\n");
              while (parser.hasMoreCommands()) {
                 String line = parser.advanceIgnoreComments();
             
                 writer.append(writer.toXML(line, name));
             
              }
          writer.append("</tokens>" + "\n");
       }
           else if(name.contains("T.xml")){
          //takes tokenizer (T.xml), outputs a complied .xml file
          Writer writer = new Writer(name.replace("T.xml",".xml"));
              MyCompiler compiler = new MyCompiler(file);
              writer.append(compiler.compile());
             
       }
       else{System.out.println("Cannot read this file type");}
        }
 
    }
    else {System.out.println(" Command line need a jack file!");}
    
   }

}

class Parser {
   String name;
   String line = "";
   Scanner sc;
   Parser (String name) {
     try {
         sc = new Scanner(new File(name));

    } catch (Exception ex ){
      System.out.println(ex.toString());
     }
   }
   Parser (File file) {
     try {
         sc = new Scanner(file);

    } catch (Exception ex ){
      System.out.println(ex.toString());
     }
   }
   boolean hasMoreCommands(){
       return sc.hasNextLine();// needs to be changed
    }
   String advance(){
      this.line = sc.nextLine();
      return this.line;
  }
  String advanceIgnoreComments() {
      boolean comment;
    this.line = sc.nextLine().trim(); 
    if (((this.line.length() > 2) && this.line.substring(0, 2).contains("//"))
        ||
        (this.line.length() < 1) || this.line == "\n"){
          return this.advanceIgnoreComments(); 
    }
    else if (this.line.contains("/**")){
         while(!(this.line.contains("*/"))){
           this.line = sc.nextLine().trim();
         }
         return this.advanceIgnoreComments();
    }
    if(this.line.contains("//") && this.line.indexOf("//") > 2){
        this.line = this.line.substring(0, this.line.indexOf("//")).trim();
    }
    return this.line;
  }
}

class Writer {
    String fileName;
    int labelCount = 0;
    Writer(String fileName){
        this.fileName = fileName;
        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
              System.out.println("File created: " + file.getName());
              return;
            }
            System.out.println("File Exists");
          }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    public void rewrite(String toWrite){
        try {
            FileWriter myWriter = new FileWriter(this.fileName);
            myWriter.write(toWrite);
            myWriter.close();
            System.out.println("["+this.fileName+"] -> Successfully Rewritten:\n"+toWrite);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    public void append(String toWrite){
        try {
            FileWriter fw = new FileWriter(this.fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(toWrite);
            //bw.newLine();
            bw.close();
            //System.out.println("["+this.fileName+"] -> Successfully Wrote:\n"+toWrite);
        }
        catch(IOException e){

        }
    }

   public String toXML(String line, String name){
   String code = line;
   String keyword = "class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return|";
   String symbol = "[\\{\\}\\(\\)\\[\\]\\.\\,\\;\\+\\-\\*\\/\\&\\|\\<\\>\\=\\~]";
   String intConstant = "[0-9]+";
   String string = "\"[^\"\n]*\"";
   String identifier = "[a-zA-Z][a-zA-Z0-9]*";
   String toReturn = "";

    Pattern pattern = Pattern.compile(keyword + symbol + "|" + intConstant + "|" + string + "|" + identifier);
    Matcher matcher = pattern.matcher(code);

    ArrayList<String> tokens = new ArrayList<String>();

    while (matcher.find()){
      tokens.add(matcher.group());
    }

    for (String str : tokens) {

    if(Pattern.matches(keyword, str)){
        toReturn = toReturn + "<keyword>"  + str + "</keyword>" + "\n";
    }
    else if(Pattern.matches(symbol, str)){
        toReturn = toReturn + "<symbol>" + str + "</symbol>" + "\n";
    }
    else if(Pattern.matches(intConstant, str)){
        toReturn = toReturn + "<intConstant>"  + str + "</intConstant>" + "\n";
    }
    else if(Pattern.matches(string, str)){
        toReturn = toReturn + "<string>" + str + "</string>" + "\n";
    }
    else if(Pattern.matches(identifier, str)){
        toReturn = toReturn +  "<identifier>" + str + "</identifier>" + "\n";
    }
       
    }
    return toReturn;
  }

   public String compiler(String line){
      return line;
   }

}
