import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class RenderThread extends Thread{
    private Farm farm;
    private int length;
    private int width;
    private AtomicBoolean flag;
    private Lock[][] locks;
    private Random ran = new Random();

    public RenderThread(Farm farm, int length, int width, AtomicBoolean flag, Lock[][] locks){
        this.farm = farm;
        this.length = length;
        this.width = width;
        this.flag = flag;
        this.locks = locks;
    }

    @Override
    public void run(){
        System.out.println("\033[H\033[2J");
        //System.out.flush();
        while (flag.get()) {
            render();
            renderWaits();
        }
    }



    private void render(){

        //trying to lock the 3x3 subarray
        
        List<Lock> listOfLocks = new ArrayList<>();
        try{
        for(int i = 0; i<this.length; ++i){
            for(int j = 0; j<this.width; ++j){
                

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

        StringBuilder sb; 

            for(int i = 0; i < this.length; ++i){
                sb = new StringBuilder("|");
                for(int j = 0; j < this.width; ++j){
                    sb.append(this.farm.getField(i, j).toString() + "|");
                }
                System.out.println(sb);
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

    private void renderWaits() {
        try {
            Thread.sleep(200);
            System.out.println("\u001B[0;0H");
            //System.out.flush();
        } catch (InterruptedException e) {
            //e.printStackTrace();
            System.out.printf("");
        }
    }
}
