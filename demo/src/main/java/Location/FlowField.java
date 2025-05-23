package Location;

public class FlowField {
    private int L,R,U,D;
    private byte[][] field;
    public FlowField(int L,int R,int U,int D,byte[][] field){
        this.L=L;this.R=R;this.U=U;this.D=D;this.field=field;
    }
}
