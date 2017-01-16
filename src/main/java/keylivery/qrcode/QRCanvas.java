package keylivery.qrcode;

import com.google.zxing.common.BitMatrix;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class QRCanvas extends Canvas{

    BitMatrix bitMatrix;

    public QRCanvas() {
    }

    public QRCanvas(double width, double height) {
        super(width, height);
    }

    public void init(BitMatrix bitMatrix){
        this.bitMatrix = bitMatrix;
    }

    public void drawQR(){
        double width = this.getWidth();
        double height = this.getHeight();
        GraphicsContext theContext = this.getGraphicsContext2D();

        theContext.setFill(Color.WHITE);
        theContext.fillRect(0, 0, width, height);
        theContext.setFill(Color.BLACK);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (bitMatrix.get(i, j)) {
                    theContext.fillRect(i, j, 1, 1);
                }
            }
        }


    }
}
