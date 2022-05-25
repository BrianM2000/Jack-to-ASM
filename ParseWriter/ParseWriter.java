
import java.io.*;
import java.util.Scanner;

public class ParseWriter {
  public static void main(String[] args) throws Exception
  {
    // pass the path to the file as a parameter
    if (args.length > 0) {
       String name = args[0];
       File file = new File(args[0]);
       
       if(file.isDirectory()){
           File[] listOfFiles = file.listFiles();
           Writer writer = new Writer(name + ".asm");
           
           for(File files : listOfFiles){
               String fileName = files.getName();
		if(fileName.contains(".vm")){
		       Parser parser = new Parser(files);
		       writer.append("//" + fileName);
		        while (parser.hasMoreCommands()) {
		         String line = parser.advanceIgnoreComments();
		         
		         writer.append(writer.toASM(line, fileName));
			}
		
                }
		else{}
                //writer.append("\n");
            }
            
       }
        
       else{
           Parser parser = new Parser(name);
           Writer writer = new Writer(name.replace(".vm",".asm"));
    
           while (parser.hasMoreCommands()) {
             String line = parser.advanceIgnoreComments();
             
             writer.append(writer.toASM(line, name));
             
           }
        }
       //writer.close();
    }
    else System.out.println(" Command line need a vm file!");
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
    this.line = sc.nextLine().trim();
    if (((this.line.length() > 2) && this.line.substring(0, 2).contains("//"))
        ||
        (this.line.length() < 1) || this.line == "\n"){
          return this.advanceIgnoreComments();
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
            bw.newLine();
            bw.close();
            //System.out.println("["+this.fileName+"] -> Successfully Wrote:\n"+toWrite);
        }
        catch(IOException e){

        }
    }
    
    /*public void close(){
        try{
            FileWriter fw = new FileWriter(this.fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);   
            bw.close();
        }
        catch(IOException e){}
    }*/
    
    public String toASM(String line, String name){
        String first = "";
        String arg1 = "";
        String arg2 = "";
        int arg2Int = 0;
        
        if(line.contains(" ")){ 
            first = line.substring(0, line.indexOf(" "));
            try{
                arg1 = line.substring(line.indexOf(" ") + 1, line.indexOf(" ", line.indexOf(" ") + 1));
            }
            catch(StringIndexOutOfBoundsException e){
                arg1 = line.substring(line.indexOf(" ") + 1, line.length());
            }
            try{
		arg2 = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.indexOf(" ",line.indexOf(" ", line.indexOf(" ") + 1) + 1));
		}
            catch(StringIndexOutOfBoundsException e2){
		try{
		        arg2 = line.substring(line.indexOf(" ", line.indexOf(" ") + 1) + 1, line.length());
		    }
		    catch(StringIndexOutOfBoundsException e1){
		        arg2 = "";
		    }
	    }            
            switch(arg1){
                case "constant": arg1 = "constant";
                break;
                
                case "argument": arg1 = "@ARG";
                break;
                
                case "local": arg1 = "@LCL";
                break;
                
                case "this": arg1 = "@THIS";
                break;
                
                case "that": arg1 = "@THAT";
                break;
                
                case "temp": arg1 = "@5";
                break;
                
                case "pointer": arg1 = "@3";
                break;
                
                case "static": arg1 = "@" + name.substring(0, name.indexOf(".")).toLowerCase() + arg2;
                break;
                
                default: break;
            }
            
            if(first == "call"){
                
            }
            else if(first == "function"){
                
            }
            else{
            
        }
        }
        else{
            first = line;
        }
        
        switch (first){
             case "add": line = "//" + line + String.join("\n", "", "@SP", "A=M","A=A-1", "D=M", "A=A-1", "M=M+D", "@SP", "M=M-1");
             break;
             
             case "sub": line = "//" + line + String.join("\n", "", "@SP", "A=M", "A=A-1", "D=M", "A=A-1", "M=M-D", "@SP", "M=M-1");
             break;
             
             case "and": line = "//" + line + String.join("\n", "", "@SP", "A=M", "A=A-1", "D=M", "A=A-1","M=M&D", "@SP", "M=M-1");
             break;
             
             case "or": line = "//" + line + String.join("\n", "", "@SP","A=M", "A=A-1", "D=M", "A=A-1", "M=M|D", "@SP", "M=M-1");
             break;
             
             case "not": line = "//" + line + String.join("\n", "", "@SP", "A=M", "A=A-1", "M=!M");
             break;
             
             case "neg": line = "//" + line + String.join("\n", "", "@SP", "A=M", "A=A-1", "M=-M");
             break;
             
             case "eq" : line = "//" + line + String.join("\n", "", "@SP", "A=M", "D=M", "A=A-1", "D=M-D", this.startLabel(), 
             "D;JEQ", "D=0", "---", this.startLabel(), "0;JMP", this.endLabel(this.labelCount-2), "D=-1", this.endLabel(this.labelCount-1), "---", "@SP", "M=M-1", "A=M-1", "A=M", "A=A-1", "M=D");
             break;
             
             case "lt" : line = "//" + line + String.join("\n", "", "@SP", "A=M", "D=M", "A=A-1", "D=M-D", this.startLabel(), 
             "D;JLT", "D=0", "---", this.startLabel(), "0;JMP", this.endLabel(this.labelCount-2), "D=-1", this.endLabel(this.labelCount-1), "---", "@SP", "M=M-1", "A=M-1", "A=M", "A=A-1", "M=D");
             break;
             
             case "gt" : line = "//" + line + String.join("\n", "", "@SP", "A=M", "D=M", "A=A-1", "D=M-D", this.startLabel(), 
             "D;JGT", "D=0", "---", this.startLabel(), "0;JMP", this.endLabel(this.labelCount-2), "D=-1", this.endLabel(this.labelCount-1), "---", "@SP", "M=M-1", "A=M-1", "A=M", "A=A-1", "M=D");
             break;
             
             case "push":
                if (arg1 == "constant") {
                    line = "//" + line + String.join("\n", "", "@" + arg2, "D=A", "@SP", "A=M", "M=D", "@SP", "M=M+1");
                }
                else{
                    line = "//" + line + String.join("\n", "", arg1, "D=M", "@" + arg2, "A=A+D", "D=M", "@SP", "A=M", "M=D", "@SP", "M=M+1");
                }
             break;
             
             case "pop": 
             if(arg1 == "constant"){
                 throw new RuntimeException("cannot pop constant, that doesn't make sense!");
                }
             else{
                 line = "//" + line + String.join("\n", "", arg1, "D=M", "@" + arg2, "D=A+D", "@R13", "M=D", "@SP", "A=M", "A=A-1", "D=M", "@R13", "A=M", "M=D", "@SP", "M=M-1");
                }
                 break;
             
             case "label" : line = "//" + line + String.join("\n", "", this.endLabel(arg1));
             break;
             
             case "goto" : line = "//" + line + String.join("\n", "", this.startLabel(arg1), "0;JMP");
             break;
             
             case "if-goto": line = "//" + line + String.join("\n", "", "@SP", "M=M-1", "A=M", "D=M", this.startLabel(arg1), "D;JNE");
             break;
             
             case "call": 
                arg2Int = Integer.parseInt(arg2) + 5;
                arg2 = String.valueOf(arg2Int);
                line = "//" + line + String.join("\n", "", "@" + arg1, "D=A", "@SP", "A=M", "M=D", "@SP", "M=M+1", "@LCL", "D=M", "@SP", "A=M", 
                "M=D", "@SP", "M=M+1", "@ARG", "D=M", "@SP", "A=M", "M=D", "@SP", "M=M+1", "@THIS", "D=M", "@SP", "A=M", "M=D", "@SP", "M=M+1", "D=M", 
                "@" + arg2, "D=D-A", "@ARG", "M=D", "@SP", "D=M", "@LCL", "M=D", "@" + arg1, "0;JMP", "(" + arg1 + ")");
             break;
             
             case "function": 
                arg2Int = Integer.parseInt(arg2);
                line = "//" + line + String.join("\n", "", "(" + arg1 + ")", this.push0(arg2Int));
             break;
             
             case "return": line = "//" + line + String.join("\n", "", "@LCL", "D=M", "@FRAME", "M=D", "@5", "D=D-A", "A=D", "D=M", "@RET", "M=D", "@SP",
             "M=M-1", "A=M", "D=M", "@ARG", "A=M", "M=D", "@ARG", "D=M+1", "@SP", "M=D", "@FRAME", "D=M", "@1", "D=D-A", "A=D", "D=M", "@THAT", "M=D", "@FRAME",
             "D=M", "@2", "D=D-A", "A=D", "D=M", "@THIS", "M=D", "@FRAME", "D=M", "@3", "D=D-A", "A=D", "D=M", "@ARG", "M=D", "@FRAME", "D=M", "@4", "D=D-A", 
             "A=D", "D=M", "@LCL", "M=D", "@RET", "A=M", "0;JMP");
             break;
             
             default: line = "unknown command";
             break;
            }
        
        return line;
    }
    
    public String startLabel(){
        int num = this.labelCount;
        ++this.labelCount;
        return "@label_" + num;
    }
    
    public String startLabel(String label){
        return "@" + label;
    }
    
    public String endLabel(String label){
        return "(" + label + ")";
    }
    
    public String endLabel(int count){
        
        return "(label_" + count + ")";
    }
    
    public String push0(int k){
        String string = "";
        int i;
        for(i = 0; i < k; ++i){
            string = string + String.join("\n", "", "@0", "D=A", "@SP", "A=M", "M=D", "@SP", "M=M+1");
        }
        return string; 
    }
}
