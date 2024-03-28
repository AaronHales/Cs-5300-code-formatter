package submit.ast;


public class ParamID implements Node {

    private String id;

    public ParamID(String id) {
        this.id = id;
    }


    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(id);
    }
}
