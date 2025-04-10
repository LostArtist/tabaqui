package model;

/**
 * Code snippet class construction
 */
public class CodeSnippet {

    private String instruction;
    private String input = "";
    private String output;

    public CodeSnippet(String instruction, String output) {
        this.instruction = instruction;
        this.output = output;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
