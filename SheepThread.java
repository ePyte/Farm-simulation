import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class SheepThread extends Thread{
        private Farm farm;
        private String id;
        private Sheep sheep;
        private Random ran = new Random();
        private AtomicBoolean flag;
        private StringBuilder winner;
        private Lock[][] locks;



    public SheepThread(Farm farm, Sheep sheep, AtomicBoolean flag, StringBuilder winner, Lock[][] locks){
        this.farm = farm;
        this.id = sheep.toString();
        this.sheep = sheep;
        this.flag = flag;
        this.winner = winner;
        this.locks = locks;
    }

    @Override
    public void run(){
        while (flag.get()) {
            move();
            sheepWaits();
        }
    }

    private void  move(){
        int sX= sheep.getX();
        int sY = sheep.getY();
        int[] newCoords = {-1, -1};


        //trying to lock the 3x3 subarray
        List<Lock> listOfLocks = new ArrayList<>();
        try {
            for(int i = sX-1; i<=sX+1; ++i){
                for(int j = sY-1; j<=sY+1; ++j){

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



            checkForDogs(newCoords, sX, sY);
            if(newCoords[0] == -1){//there are no dogs around the sheep
                int[][] possibleOffsets = {{-1,1}, {0,1}, {1,1},{-1,0}, {1,0}, {-1,-1}, {0,-1}, {1,-1}};
                int dx;
                int dy;
                do{
                    int randNum = ran.nextInt(8);
                    while(possibleOffsets[randNum][0]==-5) {
                        randNum = ran.nextInt(8);
                    }
        
                    dx = possibleOffsets[randNum][0];
                    dy = possibleOffsets[randNum][1];
                    if(checkField(this.farm.getField(sX+dx, sY+dy), sX+dx, sY+dy)){
                        newCoords[0] = sX+dx;
                        newCoords[1] = sY+dy;
                        break;
                    }
                }while(checkPossibleOffset(possibleOffsets));
            }
            
            if(newCoords[0] == -1 || newCoords[1] == -1){
                return;
            }
    
                boolean gameOver = isGameOver(newCoords[0], newCoords[1]);
                this.sheep.setX(newCoords[0]);
                this.sheep.setY(newCoords[1]);
                farm.setField(newCoords[0], newCoords[1], this.sheep);
                farm.setField(sX, sY, new Empty());
                if(gameOver){
                    this.flag.set(false);
                    this.winner.append(this.id);
                    this.interrupt();
                }



        } catch (InterruptedException e) {
           System.out.println("");
            //e.printStackTrace();
        }finally{
            for(Lock lock : listOfLocks){
                lock.unlock();
            }
        }
    }















    


    private boolean isGameOver(int x, int y){
        return (this.farm.getField(x, y)) instanceof Gate;
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
            if(field instanceof Empty || field instanceof Gate){
                return true;
            }
            return false;
        }
    

    private void checkForDogs(int[] newCoords, int sX, int sY){
        if((this.farm.getField(sX-1, sY-1) instanceof Dog) || (this.farm.getField(sX, sY-1) instanceof Dog) || (this.farm.getField(sX+1, sY-1) instanceof Dog)){
            List<Integer> possibilities = new ArrayList<>();
            if((this.farm.getField(sX-1, sY+1) instanceof Empty) || (this.farm.getField(sX-1, sY+1) instanceof Gate)){
                possibilities.add(-1);
            }
            if((this.farm.getField(sX+0, sY+1) instanceof Empty) || (this.farm.getField(sX+0, sY+1) instanceof Gate)){
                possibilities.add(0);
            }
            if((this.farm.getField(sX+1, sY+1) instanceof Empty) || (this.farm.getField(sX+1, sY+1) instanceof Gate)){
                possibilities.add(+1);
            }
            if(possibilities.size()>0){
                int rndNum = ran.nextInt(possibilities.size());
                newCoords[0] = sX+possibilities.get(rndNum);
                newCoords[1] = sY+1;
                return;
            }
        }
        //check for dogs in bottom row
        if((this.farm.getField(sX-1, sY+1) instanceof Dog) || (this.farm.getField(sX, sY+1) instanceof Dog) || (this.farm.getField(sX+1, sY+1) instanceof Dog)){
            List<Integer> possibilities = new ArrayList<>();
            if((this.farm.getField(sX-1, sY-1) instanceof Empty) || (this.farm.getField(sX-1, sY-1) instanceof Gate)){
                possibilities.add(-1);
            }
            if((this.farm.getField(sX+0, sY-1) instanceof Empty) || (this.farm.getField(sX+0, sY-1) instanceof Gate)){
                possibilities.add(0);
            }
            if((this.farm.getField(sX+1, sY-1) instanceof Empty) || (this.farm.getField(sX+1, sY-1) instanceof Gate)){
                possibilities.add(+1);
            }
            if(possibilities.size()>0){
                int rndNum = ran.nextInt(possibilities.size());
                newCoords[0] = sX+possibilities.get(rndNum);
                newCoords[1] = sY-1;
                return;
            }
        }

        //check for dog in left field
        if(this.farm.getField(sX-1, sY) instanceof Dog){
            List<Integer> possibilities = new ArrayList<>();
            if((this.farm.getField(sX+1, sY+1) instanceof Empty) || (this.farm.getField(sX+1, sY+1) instanceof Gate)){
                possibilities.add(+1);
            }
            if((this.farm.getField(sX+1, sY+0) instanceof Empty) || (this.farm.getField(sX+1, sY+0) instanceof Gate)){
                possibilities.add(0);
            }
            if((this.farm.getField(sX+1, sY-1) instanceof Empty) || (this.farm.getField(sX+1, sY-1) instanceof Gate)){
                possibilities.add(-1);
            }
            if(possibilities.size()>0){
                int rndNum = ran.nextInt(possibilities.size());
                newCoords[0] = sX+1;
                newCoords[1] = sY+possibilities.get(rndNum);
                return;
            }
        }

        //check for dog in right field
        if(this.farm.getField(sX+1, sY) instanceof Dog){
            List<Integer> possibilities = new ArrayList<>();
            if((this.farm.getField(sX-1, sY+1) instanceof Empty) || (this.farm.getField(sX-1, sY+1) instanceof Gate)){
                possibilities.add(+1);
            }
            if((this.farm.getField(sX-1, sY+0) instanceof Empty) || (this.farm.getField(sX-1, sY+0) instanceof Gate)){
                possibilities.add(0);
            }
            if((this.farm.getField(sX-1, sY-1) instanceof Empty) || (this.farm.getField(sX-1, sY-1) instanceof Gate)){
                possibilities.add(-1);
            }
            if(possibilities.size()>0){
                int rndNum = ran.nextInt(possibilities.size());
                newCoords[0] = sX-1;
                newCoords[1] = sY+possibilities.get(rndNum);
                return;
            }
        }
    }

    private void sheepWaits() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            //e.printStackTrace();
            System.out.printf("");
        }
    }
}