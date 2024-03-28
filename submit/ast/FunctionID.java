package submit.ast;

public class FunctionID implements Node{

    private String id;

    public FunctionID(String id) {
        this.id = id;
    }


    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(id);
    }
}
