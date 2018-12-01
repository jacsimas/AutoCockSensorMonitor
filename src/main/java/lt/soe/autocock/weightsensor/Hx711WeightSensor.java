package lt.soe.autocock.weightsensor;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class Hx711WeightSensor {

    private static final int PLASTIC_COCKTAIL_GLASS_4OZ_IN_GRAMS = 33;
    private static final int SMALL_COCKTAIL_GLASS_4OZ_IN_GRAMS = 100;
    private static final int FULL_SIZE_COCKTAIL_GLASS_6OZ_IN_GRAMS = 217;

    private static final int[] COCKTAIL_GLASSES = {
            PLASTIC_COCKTAIL_GLASS_4OZ_IN_GRAMS,
            SMALL_COCKTAIL_GLASS_4OZ_IN_GRAMS,
            FULL_SIZE_COCKTAIL_GLASS_6OZ_IN_GRAMS
    };

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket publisher = context.createSocket(ZMQ.PUB);
            publisher.bind("tcp://*:5556");

            //  Ensure subscriber connection has time to complete and
            //  add more time for the glass to be put on the weight sensor.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }

            //  Send one random update per second
            Random rand = new Random(System.currentTimeMillis());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publisher.send();
            }
        }
    }

    private static int getRandomCocktailGlass() {
        int randomIndex = new Random().nextInt(COCKTAIL_GLASSES.length);
        return COCKTAIL_GLASSES[randomIndex];
    }

}
