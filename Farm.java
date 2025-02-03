import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Farm {
    private Field[][] farm;
    private int width = 14;
    private int length = 14;
    private int numOfDogs = 5;
    private int numOfSheeps = 10;
    private List<String> nameOfDogs = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
    private List<Dog> listOfDogs = new ArrayList<>();
    private List<String> nameOfSheeps = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"));
    private List<Sheep> listOfSheeps = new ArrayList<>();
    private Lock[][] locks;
    

    private Random ran = new Random();

    public Farm(){
        this.farm = new Field[this.width][this.length];
        initalizeFarm();
    }

    public Field getField(int x, int y){
        return farm[y][x];
    }

    public void setField(int x, int y, Field field){
        this.farm[y][x]=field;
    }
        
    private void initalizeFarm() {
        initalizeEmptyFields();
        initalizeWalls();
        initalizeGates();
        initalizeDogs();
        initalizeSheeps();
    }
        
    private void initalizeEmptyFields(){
        for(int i = 0; i < this.length; ++i){
            for(int j = 0; j < this.width; ++j){
                this.farm[i][j] = new Empty();
            }
        }
    }

    private void initalizeWalls(){
        //Top and bottom walls
        for(int i = 0; i < this.width; ++i){
                this.farm[0][i] = new Wall();
                this.farm[this.length-1][i] = new Wall();
        }
        //Side-walls; corners are initialized 2-times
        for(int i = 0; i < this.length; ++i){
            this.farm[i][0] = new Wall();
            this.farm[i][this.width-1] = new Wall();
        }
    }
    private void initalizeGates(){

        int nxt = this.ran.nextInt(this.width-3)+1;//index 0 cannot be; neither 13
        this.farm[0][nxt] = new Gate();
        nxt = this.ran.nextInt(this.width-3)+1;
        this.farm[this.length-1][nxt] = new Gate();

        nxt = this.ran.nextInt(this.length-3)+1;
        this.farm[nxt][0] = new Gate();
        nxt = this.ran.nextInt(this.length-3)+1;
        this.farm[nxt][this.width-1] = new Gate();
    }

    private void initalizeDogs(){
        for(String name: nameOfDogs){
            int[] cord = new int[2];
            genereateValidCoordForDogs(cord);
            Dog dog = new Dog(name, cord[0], cord[1]);
            listOfDogs.add(dog);
            farm[cord[1]][cord[0]]=dog;

        }
    }

    private void genereateValidCoordForDogs(int[] cord){
        while(true){
            int y;
            int x;
            y = this.ran.nextInt(this.length-3)+1;//index 0 cannot be; neither 13
            if((y > (this.length - 2) / 3) && (y <= 2 * (this.length - 2) / 3)){ //if(y > 4 && y <= 8)
                int leftOrRight = this.ran.nextInt(2);
                if(leftOrRight==0){
                    x = this.ran.nextInt((this.width - 2) / 3)+1; //(1-4)
                }
                else{
                    x = this.ran.nextInt((this.width - 2) / 3)+9; //(9-12)
                }
            }
            else{
                x =  this.ran.nextInt(this.width-3)+1;
            }

            if(this.farm[y][x] instanceof Empty){
                cord[0] = x;
                cord[1] = y;
                break;
            }
        }
    }

    private void initalizeSheeps(){
        for(String name: nameOfSheeps){
            int[] cord = new int[2];
            genereateValidStartCoordForSheeps(cord);

            Sheep sheep = new Sheep(name, cord[0], cord[1]);
            listOfSheeps.add(sheep);
            farm[cord[1]][cord[0]]=sheep;
        }
    }

    private void genereateValidStartCoordForSheeps(int[] cord){
        int x;
        int y;
        while(true){
            x = this.ran.nextInt((this.width - 2) / 3)+5;//5-8
            y = this.ran.nextInt((this.length - 2) / 3)+5;

            if(this.farm[y][x] instanceof Empty){
                cord[0] = x;
                cord[1] = y;
                break;
            }
        }
    }
    
    private void initLocks(){
        this.locks=new ReentrantLock[this.length][this.width];
        for(int i = 0; i<this.length; ++i){
            for(int j = 0; j<this.width; ++j){
                this.locks[i][j] = new ReentrantLock(true);
                };
            }
        }

    public void threadOperations(){

        initLocks();//idea: have a 2D lock array; use tryLock with different time limits


        StringBuilder winnerSheep = new StringBuilder("");
        AtomicBoolean flag = new AtomicBoolean(true);
        RenderThread renderThread = new RenderThread(this, this.length, this.width, flag, this.locks);
        renderThread.start();

        DogThread[] dogThreads = new DogThread[this.numOfDogs];
        for (int i = 0; i < this.numOfDogs;++i){
            dogThreads[i] = new DogThread(this, listOfDogs.get(i), flag, this.locks);
            dogThreads[i].start();
        }

        SheepThread[] sheepThreads = new SheepThread[this.numOfSheeps];
        for (int i = 0; i < this.numOfSheeps;++i){
            sheepThreads[i] = new SheepThread(this, listOfSheeps.get(i), flag, winnerSheep, this.locks);
            sheepThreads[i].start();
        }

        while(flag.get()){//wait for flag to turn false -> the SheepThread of first escaping sheep will change it
        }

        renderThread.interrupt();

        for (int i = 0; i < this.numOfDogs;++i){
            dogThreads[i].interrupt();
        }

        for (int i = 0; i < this.numOfSheeps;++i){
            sheepThreads[i].interrupt();
        }

        System.out.println("Game Over");
        System.out.println("Sheep " + winnerSheep + " has left the area." );
        System.out.flush();
    }
}

