package project.service.amineservice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Alert;
import project.models.aminemodels.Reservation; // Importez la classe Reservation

import java.awt.image.BufferedImage;

public class QRCodeService {

    public ImageView generateQRCodeImageView(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            WritableImage qrImage = SwingFXUtils.toFXImage(bufferedImage, null);
            return new ImageView(qrImage);
        } catch (WriterException | IllegalArgumentException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Could not generate QR code");
            errorAlert.setContentText("An error occurred while generating the QR code: " + e.getMessage());
            errorAlert.showAndWait();
            return null; // Or handle the error differently
        }
    }

    public ImageView generateReservationQRCode(Reservation reservation) {
        StringBuilder qrContent = new StringBuilder();
        qrContent.append(reservation.getId()).append(",");
        qrContent.append(reservation.getUserName()).append(",");
        qrContent.append(reservation.getEmail()).append(",");
        qrContent.append(reservation.getContact()).append(",");
        if (reservation.getRehla() != null) {
            qrContent.append(reservation.getRehla().getDepart()).append(",");
            qrContent.append(reservation.getRehla().getDestination()).append(",");
            qrContent.append(reservation.getRehla().getPrice()).append(",");
            if (reservation.getRehla().getAgence() != null) {
                qrContent.append(reservation.getRehla().getAgence().getNom());
            }
        }
        return generateQRCodeImageView(qrContent.toString());
    }
}