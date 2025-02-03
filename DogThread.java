import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class DogThread extends Thread{
        private Farm farm;
        private String id;
        private Dog dog;
        private Random ran = new Random();
        private AtomicBoolean flag;
        private Lock[][] locks;


    public DogThread(Farm farm, Dog dog, AtomicBoolean flag, Lock[][] locks){
        this.farm = farm;
        this.id = dog.toString();
        this.dog = dog;
        this.flag = flag;
        this.locks = locks;

    }

    @Override
    public void run(){
        while (flag.get()) {
            move();
            dogWaits();
        }
    }

    private void move(){
        //idea: to avoid infinite loop all possible positions (8) will be listed in a 2D array
        //the thread will generate a random number between [0-7]
        //if a position is checked, and invalid the value will be overwritten
        //if all values have been overvritten the thread leaves the cycle and finishes blocking the farm
        int dogX= dog.getX();
        int dogY = dog.getY();
        int[][] possibleOffsets = {{-1,1}, {0,1}, {1,1},{-1,0}, {1,0}, {-1,-1}, {0,-1}, {1,-1}};
        int dx;
        int dy;

        //trying to lock the 3x3 subarray
        List<Lock> listOfLocks = new ArrayList<>();
        try{
            for(int i = dogX-1; i<=dogX+1; ++i){
                for(int j = dogY-1; j<=dogY+1; ++j){
                    
                        if(locks[i][j].tryLock(50, TimeUnit.MILLISECONDS)){
                        listOfLocks.add(locks[i][j]);
                        }
                        else{
                            for(Lock lock : listOfLocks){
                                lock.unlock();
                            }

                            return;
                        }
                }
            }

            do{
                int randNum = ran.nextInt(8);
                while(possibleOffsets[randNum][0]==-5) {
                    randNum = ran.nextInt(8);
                }
    
                dx = possibleOffsets[randNum][0];
                dy = possibleOffsets[randNum][1];
                if(checkField(this.farm.getField(dogX+dx, dogY+dy), dogX+dx, dogY+dy)){
                    this.dog.setX(dogX+dx);
                    this.dog.setY(dogY+dy);
                    farm.setField(dogX+dx, dogY+dy, this.dog);
                    farm.setField(dogX, dogY, new Empty());
                    break;
                }
            }while(checkPossibleOffset(possibleOffsets));


        } catch (InterruptedException e) {
            System.out.println("");
             //e.printStackTrace();
         }finally{
             for(Lock lock : listOfLocks){
                 lock.unlock();
             }
         }
    }


        private boolean checkPossibleOffset(int[][] possibleOffsets){
            for(int i = 0; i<possibleOffsets.length; ++i){
                if(possibleOffsets[i][0] == -5){
                    return true;
                }
            }

            return false;
        }

        private boolean checkField(Field field, int x, int y){
            if(isDogArea(x,y)){
                if(field instanceof Empty){
                    return true;
                }
            }
            return false;
        }

        private boolean isDogArea(int x, int y){
            if(y<5 || y>8){
                return true;
            }

            if(x<5 || x>8){
                return true;
            }

            if(y>=5 && y<=8 && (x<5 || x>8)){
                return true;
            }

            if(x>=5 && x<=8 && (y<5 || y>8)){
                return true;
            }

            return false;
        }

        private void dogWaits() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                System.out.printf("");
            }
        }
}
