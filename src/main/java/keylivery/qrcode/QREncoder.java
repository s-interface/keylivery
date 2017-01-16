package keylivery.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

public class QREncoder {

    String myCodeText;
    int codeWidth;


    public QREncoder(String myCodeText, int codeWidth) {
        this.myCodeText = myCodeText;
        this.codeWidth = codeWidth;
    }

    public BitMatrix encode(){

        Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
        hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, codeWidth,
                    codeWidth, hintMap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitMatrix;
    }
}
