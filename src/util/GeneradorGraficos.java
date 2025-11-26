package util;

import Modelo.DatoGrafico;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Font; // Nuevo import para fuentes
import java.io.File;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.FileOutputStream;
import java.util.List;
import org.jfree.chart.plot.CategoryPlot; // Nuevo import para el plot
import org.jfree.chart.renderer.category.BarRenderer; // Nuevo import para las barras
import org.jfree.chart.axis.CategoryAxis; // Nuevo import para el eje
import org.jfree.chart.ui.RectangleInsets; // Nuevo import para márgenes


public class GeneradorGraficos {

    // 1. Crear el Panel con el Gráfico (Estilizado)
    public ChartPanel crearGraficoBarras(List<DatoGrafico> datos, String titulo) {
        // Llenar los datos
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DatoGrafico d : datos) {
            // Aseguramos que los nombres largos se vean bien en el gráfico horizontal
            dataset.addValue(d.getValor(), "Bien", d.getNombre()); 
        }

        // Crear el gráfico base
        JFreeChart chart = ChartFactory.createBarChart(
                titulo,                     // Título
                "Bienes",                // Eje X
                "Cantidad Actual",             // Eje Y
                dataset,
                PlotOrientation.HORIZONTAL, // Barras horizontales
                true, true, false
        );

        // --- Personalización ---
        
        // 1. Plot (Área central del gráfico)
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245)); // Fondo claro del plot
        plot.setDomainGridlinePaint(Color.gray); // Líneas de cuadrícula verticales
        plot.setRangeGridlinePaint(Color.lightGray); // Líneas de cuadrícula horizontales
        plot.setOutlinePaint(null); // Quitar el borde feo del plot

        // 2. Título y Fuentes
        chart.setBackgroundPaint(Color.white); // Fondo blanco
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // 3. Ejes
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14)); // Fuente del título del eje
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12)); // Fuente de las etiquetas (nombres de artículo)

        // Ocultar etiquetas del eje X (si es horizontal, el eje X es el de las cantidades)
        plot.getRangeAxis().setTickLabelsVisible(true);
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));

        // 4. Renderizador (Barras)
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // Color principal azul moderno para las barras (RGB: 79, 129, 189)
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); 
        renderer.setDrawBarOutline(false); // Quitar el borde negro de las barras
        renderer.setMaximumBarWidth(0.08); // Barras un poco más delgadas
        
        // 5. Devolver el panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setMinimumDrawHeight(0); // Aseguramos que se dibuje correctamente
        panel.setMinimumDrawWidth(0); 
        return panel;
    }

    // 2. Exportar a PDF (el código es el mismo, pero lo dejo completo para referencia)
    public void exportarPDF(ChartPanel panel, File archivoDestino) throws Exception {
        // Paso A: Guardar gráfico como imagen temporal
        File tempImage = new File("temp_grafico.png");
        // Usar un tamaño más grande para mejor resolución en PDF
        ChartUtils.saveChartAsPNG(tempImage, panel.getChart(), 1000, 700); 

        // Paso B: Crear PDF e insertar imagen
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(archivoDestino));
        doc.open();
        
        doc.add(new Paragraph("Reporte de Estadística de Bien Mobiliario"));
        doc.add(new Paragraph(" ")); 
        
        Image img = Image.getInstance(tempImage.getAbsolutePath());
        // Ajustar la imagen al ancho de la página PDF
        img.scaleToFit(doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin(),
                       doc.getPageSize().getHeight() - doc.topMargin() - doc.bottomMargin());
        doc.add(img);
        
        doc.close();
        tempImage.delete(); // Borrar temporal
    }
    
    public ChartPanel crearGraficoValorPorUbicacion(java.util.LinkedHashMap<String, Double> datos, String titulo) {
        // Llenar los datos
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Iteramos sobre el Map (Ubicación -> Valor)
        for (java.util.Map.Entry<String, Double> entry : datos.entrySet()) {
            // Aseguramos que los nombres largos se vean bien en el gráfico horizontal
            // Clave: Ubicación, Valor: Monto
            dataset.addValue(entry.getValue(), "Valor ($)", entry.getKey()); 
        }

        // Crear el gráfico base (similar al anterior, pero ajustando etiquetas)
        JFreeChart chart = ChartFactory.createBarChart(
                titulo,                             // Título
                "Ubicación",                        // Eje X (Nombres de Ubicaciones)
                "Valor Total ($)",                // Eje Y (Valores monetarios)
                dataset,
                PlotOrientation.HORIZONTAL,         // Barras horizontales
                true, true, false
        );

        // --- Personalización ---
        
        // 1. Plot (Área central del gráfico)
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setOutlinePaint(null);

        // 2. Título y Fuentes
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // 3. Ejes
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        // Eje de los valores (Y)
        plot.getRangeAxis().setTickLabelsVisible(true);
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));

        // 4. Renderizador (Barras)
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // Color principal verde/lima para distinguir del gráfico de stock (RGB: 155, 187, 89)
        renderer.setSeriesPaint(0, new Color(155, 187, 89)); 
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.08); 
        
        // 5. Devolver el panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setMinimumDrawHeight(0);
        panel.setMinimumDrawWidth(0); 
        return panel;
    }
}