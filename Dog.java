public class Dog implements Field {
    private String id;
    private int xCord;
    private int yCord;

    public Dog(String id, int xCord, int yCord){
        this.id=id;
        this.xCord = xCord;
        this.yCord = yCord;
    }

    @Override
    public String toString() {
        return this.id;//strings are immutable
    }
    
    public int getX(){
        return this.xCord;
    }

    public int getY(){
        return this.yCord;
    }

    public void setX(int xCord){
        this.xCord = xCord;
    }

    public void setY(int yCord){
        this.yCord = yCord;
    }
    
}
