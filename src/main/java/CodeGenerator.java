// visits parsetree
// generates a .j jasmine file

import analysis.ReversedDepthFirstAdapter;
import node.Start;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeGenerator extends ReversedDepthFirstAdapter {

    private File jasmin;
    private Start tree;
    private String jasminContent;

    public CodeGenerator(Start tree, String filename) {
        this.jasmin = new File(filename);
        this.tree = tree;

        try {
            FileWriter fw = new FileWriter(jasmin);

            // base jasmin code prefix
            fw.write(".bytecode 49.0\n" +
                    ".source jasmineCode.j\n" +
                    ".class public jasminCode\n" +
                    ".super java/lang/Object\n" +
                    ".method public <init>()V\n" +
                    "<bytecode>\n" +
                    ".end method\n" +
                    "\n");
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File generateCode() {
        String jasminOutput = visitParseTree(tree);

        try {
            FileWriter fw = new FileWriter(jasmin);
            fw.write(jasminOutput);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(jasminOutput);
        return jasmin;

    }

    // create jasmin-code for input
    private String visitParseTree(Start tree) {
        jasminContent = "";

        tree.apply(this);

        //TODO:
        // check 'tree' object and find a way to visit every pattern in correct way

        System.out.println(tree.toString());
    return jasminContent;
    }

}
