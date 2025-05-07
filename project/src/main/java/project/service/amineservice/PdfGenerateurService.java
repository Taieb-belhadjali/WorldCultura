package project.service.amineservice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import project.models.aminemodels.Reservation;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage; // Import pour BufferedImage
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javafx.scene.control.Alert;

public class PdfGenerateurService {

    public void generatePdfTicket(Reservation reservation) {
        String htmlContent = generateHtmlTicket(reservation);
        String pdfFileName = "ticket_reservation_" + reservation.getId() + ".pdf";

        try {
            ITextRenderer renderer = new ITextRenderer();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new InputSource(new ByteArrayInputStream(htmlContent.getBytes("UTF-8"))));
            renderer.setDocument(document, null);
            renderer.layout();

            try (OutputStream os = new FileOutputStream(pdfFileName)) {
                renderer.createPDF(os);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF Généré");
            alert.setHeaderText(null);
            alert.setContentText("Le ticket PDF a été généré avec succès : " + pdfFileName);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur PDF");
            errorAlert.setHeaderText("La génération du PDF a échoué.");
            errorAlert.setContentText("Une erreur est survenue lors de la création du PDF : " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    private String generateHtmlTicket(Reservation reservation) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String departDateFormatted = reservation.getRehla().getDepart_date() != null ?
                reservation.getRehla().getDepart_date().format(dateFormatter) : "Non définie";
        String arrivalDateFormatted = reservation.getRehla().getArrival_date() != null ?
                reservation.getRehla().getArrival_date().format(dateFormatter) : "Non définie";

        String qrCodeBase64 = generateQRCodeBase64(reservation);

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <title>Ticket de Réservation</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background: #f5f5f5;\n" +
                "        }\n" +
                "        .ticket-container {\n" +
                "            max-width: 900px;\n" +
                "            margin: 20px auto;\n" +
                "            padding: 20px;\n" +
                "            background: #fff;\n" +
                "            background-image: url('https://img.freepik.com/vecteurs-premium/illustration-silhouette-avion_77417-2028.jpg');\n" +
                "            background-size: cover;\n" +
                "            background-position: center;\n" +
                "            background-repeat: no-repeat;\n" +
                "        }\n" +
                "        .ticket-header {\n" +
                "            background: #003366;\n" +
                "            color: #fff;\n" +
                "            padding: 10px;\n" +
                "            text-align: center;\n" +
                "            font-size: 20px;\n" +
                "            font-weight: bold;\n" +
                "            border-top-left-radius: 10px;\n" +
                "            border-top-right-radius: 10px;\n" +
                "        }\n" +
                "        table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "        }\n" +
                "        th, td {\n" +
                "            padding: 8px 12px;\n" +
                "            border-bottom: none;\n" +
                "            background: rgba(255, 255, 255, 0.7);\n" +
                "        }\n" +
                "        th {\n" +
                "            text-align: left;\n" +
                "            color: #999;\n" +
                "        }\n" +
                "        td {\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .value-right {\n" +
                "            text-align: right;\n" +
                "            border-right: 2px dashed #ddd;\n" +
                "        }\n" +
                "        .ticket-footer {\n" +
                "            background: #eee;\n" +
                "            padding: 10px;\n" +
                "            text-align: center;\n" +
                "            font-size: 14px;\n" +
                "            color: #666;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "        .qr-code img {\n" +
                "            max-width: 100%;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"ticket-container\">\n" +
                "    <div class=\"ticket-header\">\n" +
                "        World Cultura\n" +
                "    </div>\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <th>Nom</th>\n" +
                "            <td>" + reservation.getUserName() + "</td>\n" +
                "            <th>Agence</th>\n" +
                "            <td class=\"value-right\">" + (reservation.getRehla().getAgence() != null ? reservation.getRehla().getAgence().getNom() : "") + "</td>\n" +
                "            <th>Nom du passager</th>\n" +
                "            <td>" + reservation.getUserName() + "</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <th>Départ</th>\n" +
                "            <td>" + reservation.getRehla().getDepart() + "</td>\n" +
                "            <th>Destination</th>\n" +
                "            <td class=\"value-right\">" + reservation.getRehla().getDestination() + "</td>\n" +
                "            <th>Destination</th>\n" +
                "            <td class=\"value-right\">" + reservation.getRehla().getDestination() + "</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <th>Date de départ</th>\n" +
                "            <td>" + departDateFormatted + "</td>\n" +
                "            <th>Date d'arrivée</th>\n" +
                "            <td class=\"value-right\">" + arrivalDateFormatted + "</td>\n" +
                "            <th>Vol</th>\n" +
                "            <td>F 0575</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <th>Prix</th>\n" +
                "            <td>" + reservation.getRehla().getPrice() + " €</td>\n" +
                "            <th>QR Code</th>\n" +
                "            <td class=\"qr-code\"><img src=\"data:image/png;base64," + qrCodeBase64 + "\" alt=\"QR Code\"/></td>\n" +
                "            <th>QR Code</th>\n" +
                "            <td class=\"qr-code\"><img src=\"data:image/png;base64," + qrCodeBase64 + "\" alt=\"QR Code\"/></td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</div>\n" +
                "<div class=\"ticket-footer\">\n" +
                "    Merci d'avoir réservé avec nous !\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }

    private String generateQRCodeBase64(Reservation reservation) {
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

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent.toString(), BarcodeFormat.QR_CODE, 90, 90);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return ""; // Retourne une chaîne vide en cas d'erreur
        }
    }
}