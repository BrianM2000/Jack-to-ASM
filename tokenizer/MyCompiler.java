import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap; 

public class MyCompiler{
    Scanner sc;
    int lineNum = 0;
    int depth = 0;
    int labelCount = 0;
    String toReturn = "";
    String curLine = "";
    String className;    
    String kind;
    SymbolTable table;
    //SymbolTable subTable;

    public MyCompiler(File file){
        try {
            sc = new Scanner(file);

        }
        catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
    
    public String compile(){
        next();
        if(curLine.startsWith("<tokens>")){
            next();
            if(curLine.startsWith("<keyword>class</keyword>")){
                append("<class>\n");
	        table = new SymbolTable();
                compileClass();
		table = null;
                append("</class>\n");
            }
            else{
                System.out.println("Error at line " + Integer.toString(lineNum) + " " + curLine);
            }
        }
        return toReturn;
    }
    
    public void compileClass(){
        ++depth;
        next();
        className = compileIdentifier();
        next();
        compileSymbol("{");
        next();
        while(curLine.contains("static") || curLine.contains("field")){
            kind = curLine.substring(9, curLine.indexOf("</keyword>"));
	    compileClassVarDec();
            next();
        }

        while(curLine.contains("constructor") || curLine.contains("function") || curLine.contains("method")){
            compileSubroutineDec();
            next();
        }
        compileSymbol("}");
        --depth;
    }

   public String compileKeyword(){
       if(curLine.startsWith("<keyword>")){
            append(curLine + "\n");
	    return curLine.substring(9, curLine.indexOf("</keyword>"));
        }
        else{
            System.out.println("Error at line " + Integer.toString(lineNum) + " " + curLine + " should be keyword");
	    return "error";        
	}
   }
    
    public String compileIdentifier(){
        if(curLine.startsWith("<identifier>")){
            append(curLine + "\n");
            return curLine.substring(12, curLine.indexOf("</identifier>"));
        }
        else{
            System.out.println("Error at line " + Integer.toString(lineNum) + " " + curLine + " should be Identifier");
	    return "error";        
	}
    }
    
    public String compileSymbol(String symbol){
        if(curLine.contains("<symbol>" + symbol + "</symbol>")){
            append(curLine + "\n");
	    return curLine.substring(8, curLine.indexOf("</symbol>"));
        }
        else{
            System.out.println("Error at line " + Integer.toString(lineNum) + " " + curLine + " should be Symbol " + symbol);
            return "error";
	    }
    }
    
    public String compileSymbol(){
        if(curLine.contains("<symbol>")){
            append(curLine + "\n");
	    return curLine.substring(8, curLine.indexOf("</symbol>"));
        }
        else{
            System.out.println("Error at line " + Integer.toString(lineNum) + " " + curLine + " should be Syboml");
	    return "error";        
	}
    }

    public int compileIntConstant(){
        if(curLine.contains("<intConstant>")){
            append(curLine + "\n");
	    return Integer.parseInt(curLine.substring(13, curLine.indexOf("</intConstant>")));
        }
        else{
            System.out.println("Error at line " + Integer.toString(lineNum) + " " + curLine + " should be intConstant");
	    return -1;       
	}
    }
    
    public String compileString(){
        if(curLine.contains("<string>")){
            append(curLine + "\n");
	    return curLine.substring(8, curLine.indexOf("</string>")); 
        }
        else{
            System.out.println("Error at line " + Integer.toString(lineNum) + " " + curLine + " should be string");
	    return "error";        
	}
    }
    
    public void compileClassVarDec(){
        append("<ClassVarDec>\n");
        ++depth;
	String type;
	String identifier;
        append(curLine + "\n");
        next();
        type = compileType();
        next();
        while(curLine.startsWith("<identifier>")){
            identifier = compileIdentifier();
	    table.add(identifier, type, kind);
            next();
            if(curLine.contains(",")){
                compileSymbol(",");
                next();
            }
        }
        compileSymbol(";");
        --depth;
        append("</ClassVarDec>\n");
    }

    public String compileType(){
	String type;
        if(curLine.startsWith("<keyword>")){
            type = compileKeyword();
        }
        
        else if (curLine.startsWith("<identifier>")){
            type = compileIdentifier();
        }
        else{
            System.out.println("Error at line " + Integer.toString(lineNum) + " " + curLine);
	    type = "Error";       
	 }
	return type;
    }
    
    public void compileSubroutineDec(){
        //table = new SymbolTable();
        append("<SubroutineDec>\n");
        ++depth;
	int numParameters = 0;
        append(curLine + "\n");
        next();
        if(curLine.contains("void")){
            compileKeyword();
        }
        else{
            compileType();
        }
        next();
        String functionName = compileIdentifier();
        next();
        compileSymbol("(");
        next();
        if(curLine.contains("int") || curLine.contains("char") || curLine.contains("boolean") || curLine.contains("<identifier>")){
            numParameters = compileParameterList();
        }
        compileSymbol(")");
        next();
	System.out.println("function " + className + "." + functionName + " " + numParameters);
        compileSubroutineBody();
        --depth;
        append("</SubroutineDec>\n");
	table.subTable = new HashMap<String, Symbol>();
    }
    
    public int compileParameterList(){
        append("</ParameterList>\n");
        ++depth;
        int numParameters = 1;
        
        String type = compileType();
        next();
        String identifier = compileIdentifier();
	table.add(identifier, type, "arg");
        next();
        while(curLine.contains(",")){
            ++numParameters;
            compileSymbol(",");
            next();
            type = compileType();
            next();
            identifier = compileIdentifier();
	    table.add(identifier, type, "arg");
            next();
        }
        --depth;
        append("</ParameterList>\n");
	return numParameters;
    }

    public void compileSubroutineBody(){
        append("<SubroutineBody>\n");
        ++depth;
        compileSymbol("{");
        next();
        while(curLine.contains("var")){
            compileVarDec();
        }
        compileStatements();
        next();
        compileSymbol("}");
        --depth;
        append("</SubroutineBody>\n");
    }
    
    public void compileVarDec(){
        append("<VarDec>\n");
        ++depth;
        append(curLine + "\n");
        next();
        String type = compileType();
        next();
        String identifier = compileIdentifier();
	table.add(identifier, type, "var");
        next();
        while(curLine.contains(",")){
            compileSymbol(",");
            next();
            identifier = compileIdentifier();
	    table.add(identifier, type, "var");
            next();
        }
        compileSymbol(";");
        next();
        --depth;
        append("</VarDec>\n");
    }
    
    public void compileStatements(){
        append("<Statements>\n");
        ++depth;
        while(curLine.contains("let") || curLine.contains("if") || curLine.contains("while")
        || curLine.contains("do") || curLine.contains("return")){
            if(curLine.contains("let")){
                compileLetStatement();
            }
            else if(curLine.contains("if")){
                compileIfStatement();
            }
            else if(curLine.contains("while")){
                compileWhileStatement();
            }
            else if(curLine.contains("do")){
                compileDoStatement();
            }
            else if(curLine.contains("return")){
                compileReturnStatement();
            }
            //System.out.println(curLine);
        }
        --depth;
        append("</Statements>\n");
    }
    
    public void compileLetStatement(){
        append("<letStatement>\n");
        ++depth;
        append(curLine + "\n");
        next();
        String identifier = compileIdentifier();
        next();
        if(curLine.contains("[")){
            compileSymbol("[");
            next();
            compileExpression();
            //next();
            compileSymbol("]");
            next();
        }
        compileSymbol("=");
        next();
        compileExpression();
        //next();
        compileSymbol(";");
        next();
        --depth;
        append("</letStatement>\n");
	System.out.println("pop " + table.kindOf(identifier) + " " + table.indexOf(identifier));
    }
    
    public void compileIfStatement(){
	int firstLabel;
	int secondLabel;
        append("<ifStatement>\n");
        ++depth;
        compileKeyword();
        next();
        compileSymbol("(");
        next();
        compileExpression();
	System.out.println("not");
	firstLabel = labelCount;
	System.out.println("if-goto label" + labelCount);
	++labelCount;
        //next();
        compileSymbol(")");
        next();
        compileSymbol("{");
        next();
        compileStatements();
        //next();
        compileSymbol("}");
	secondLabel = labelCount;
	System.out.println("goto label" + labelCount);
	++labelCount;
        next();
        if(curLine.contains("else")){
            compileKeyword();
            next();
            compileSymbol("{");
            next();
	    System.out.println("label" + firstLabel);
            compileStatements();
            //next();
            compileSymbol("}");
            next();
        }
	System.out.println("label" + secondLabel);
        --depth;
        append("</ifStatement>\n");
    }
    
    public void compileWhileStatement(){
        append("<whileStatement>\n");
        ++depth;
        compileKeyword();
        next();
        compileSymbol("(");
        next();
        compileExpression();
        //next();
        compileSymbol(")");
        next();
        compileSymbol("{");
        next();
        compileStatements();
        //next();
        compileSymbol("}");
        next();
        --depth;
        append("</whileStatement>\n");
    }
    
    public void compileDoStatement(){
        append("<doStatement>\n");
        ++depth;
        compileKeyword();
        next();
        compileSubroutineCall();
        next();
        compileSymbol(";");
        next();
        --depth;
        append("</doStatement>\n");
    }
    
    public void compileReturnStatement(){
        append("<returnStatement>\n");
        ++depth;
        compileKeyword();
        next();
        //System.out.println(isTerm(curLine));
        if(isTerm(curLine)){
            compileExpression();
            //next();
        }
	else{
	   System.out.println("push constant 0");
	}
        compileSymbol(";");
        //next();
        --depth;
        append("</returnStatement>\n");
	System.out.println("return");
    }
    
    public void compileExpression(){
        append("<expression>\n");
        ++depth;
	String op;
        compileTerm();
        next();
        while(curLine.contains("<symbol>+</symbol>") || curLine.contains("<symbol>-</symbol>") 
        || curLine.contains("<symbol>*</symbol>") || curLine.contains("<symbol>/</symbol>")
        || curLine.contains("<symbol>&</symbol>") || curLine.contains("<symbol>|</symbol>")
        || curLine.contains("<symbol><</symbol>") || curLine.contains("<symbol>></symbol>") 
        || curLine.contains("<symbol>=</symbol>")){
            op = compileOp();
            next();
            compileTerm();
            next();
	    System.out.println(op);
        }
        --depth;
        append("</expression>\n");
    }
    
    public void compileTerm(){
        append("<term>\n");
        ++depth;
        
        if(curLine.contains("<intConstant>")){
	    int i;
            i = compileIntConstant();
	    System.out.println("push constant " + i);
        }
        else if(curLine.contains("<string>")){
            compileString();
        }
        else if(curLine.contains("<keyword>")){
            compileKeyword();
        }
        else if(curLine.contains("<identifier>")){ 
            //need to find a way to determine between subroutineCall and varName [expression]
            //compileIdentifier();
            //next();
            if(sc.hasNext("\\<symbol\\>\\[\\<\\/symbol\\>")){
                compileIdentifier();
                next();
                compileSymbol("[");
                next();
                compileExpression();
                //next();
                compileSymbol("]");
            }
            else if(sc.hasNext("\\<symbol\\>\\.\\<\\/symbol\\>") || 
                    (sc.hasNext("\\<symbol\\>\\(\\<\\/symbol\\>"))){
                compileSubroutineCall();
            }
            else{
                String identifier = compileIdentifier();
		System.out.println("push " + table.kindOf(identifier) + " " +table.indexOf(identifier));
            }
        }
        else if(curLine.contains("(")){
            compileSymbol("(");
            next();
            compileExpression();
            //next();
            compileSymbol(")");
        }
        else if(curLine.contains("-") || curLine.contains("~")){
            compileUnaryOp();
        }
        
        --depth;
        append("</term>\n");
    }
    
    public void compileSubroutineCall(){
        append("<subroutineCall>\n");
        ++depth;
        String name = compileIdentifier();
	int num = 0;
        next();
        if(curLine.contains("(")){
            compileSymbol("(");
            next();
            num = compileExpressionList();
            compileSymbol(")");
        }
        else if(curLine.contains(".")){
            compileSymbol(".");
            next();
            name = (name + "." + compileIdentifier());
            next();
            compileSymbol("(");
            next();
            num = compileExpressionList();
            compileSymbol(")");
        }
        System.out.println("call " + name + " " + num);
	System.out.println("pop temp 0");
        --depth;
        append("</subroutineCall>\n");
    }
    
    public int compileExpressionList(){
        append("<expressionList>\n");
        ++depth;
	int numExpression = 0;
        if(isTerm(curLine)){
	    ++numExpression;
            compileExpression();
            //next();
            while(curLine.contains(",")){
		++numExpression;
                compileSymbol(",");
                next();
                compileExpression();
                //next();
            }
        }
        else {
            //next();
        }
        --depth;
        append("</expressionList>\n");
	return numExpression;
    }
    
    public String compileOp(){
        append("<op>\n");
        ++depth;
        String op = compileSymbol();
	String result= "";
	switch (op){
	    case "+" : result = "add"; break;
	    case "-" : result = "sub"; break;
	    case "=" : result = "eq"; break;
	    case "<" : result = "lt"; break;
	    case ">" : result = "gt"; break;
	    case "&" : result = "and"; break;
	    case "|" : result = "or"; break;
	}
        --depth;
        append("</op>\n");
	return result;
    }
    
    public String compileUnaryOp(){
	String op = "";
        append("<unaryOp>\n");
        ++depth;
        if(curLine.contains("-")){
            compileSymbol("-");
	    op = "neg";
        }
        else if(curLine.contains("~")){
            compileSymbol("~");
	    op = "not";
        }
        next();
        compileTerm();
        --depth;
        append("</unaryOp>\n");
	return op;
    }
    
    public void compileKeywordConstant(){
        append("<keywordConstant>\n");
        ++depth;
        compileKeyword();
        --depth;
        append("</keywordConstant>\n");
    }
    
    public boolean isTerm(String string){
        if(curLine.contains("<intConstant>") ||
        curLine.contains("<string>") ||
        curLine.contains("<keyword>") || 
        curLine.contains("<identifier>") ||
        curLine.contains("<symbol>(") ||
        curLine.contains("<symbol>-") || 
        curLine.contains("<symbol>~")){
            return true;
        }
        else return false;
    }
    
    public void append(String string){
        int i = 0;
        for(i = 0; i < depth; ++i){
            toReturn = toReturn + "    ";
        }
        toReturn = toReturn + string;
    }
    
    public void next(){
        if(sc.hasNextLine()){
            if(sc.hasNext("\\<\\/tokens\\>")){
                return;
            }
            lineNum ++;
            curLine = sc.nextLine();
        }  
    }
}
