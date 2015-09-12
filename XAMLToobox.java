import org.jdom2.*;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Rouslan on 17/03/2015.
 */
public class XAMLToobox {
    public static String INFILEPATH = "src/inputFiles/classResults.xml";
    public static final String OUTFILEPATH = "../OutputFileXAML.xaml";
    public static final String WINDESKTOPPATH = System.getProperty("user.home").replace("\\", "/") + "/Desktop/OutputFileXAML.xaml";
    public static final String MACDESKTOPPATH = System.getProperty("user.home") + "/Desktop/OutputFileXAML.xaml";

    private XAMLToobox(){};

    private volatile static XAMLToobox toobox;

    public static XAMLToobox getToobox(){
        if(toobox == null){
            synchronized (XAMLToobox.class){
                if(toobox == null){
                    toobox = new XAMLToobox();
                }
            }
        }
        return toobox;
    }

    /**
     * methode om gridlayout aan te maken. een array van waarden wordt megegeven voor de rows, en waarden voor een
     * array van columns.
     *
     * @param rows
     * @param columns
     * @return
     */
    public Element createGridLayout(int rows, int columns) {
        Element rootElement = createXAMLElement("Grid");
        Element columnDefinitions = createXAMLElement("Grid.ColumnDefinitions");
        for (int i = 0; i < columns; i++) {
            Element definition = createXAMLElement("ColumnDefinition");
            columnDefinitions.addContent(definition);
        }
        Element rowDefinitions = createXAMLElement("Grid.RowDefinitions");
        for (int i = 0; i < rows; i++) {
            Element definition = createXAMLElement("RowDefinition");
            rowDefinitions.addContent(definition);
        }
        rootElement.addContent(columnDefinitions);
        rootElement.addContent(rowDefinitions);
        return rootElement;
    }


    public Element createGridLayout(String[] rows, String[] columns) {
        Element rootElement = createXAMLElement("Grid");
        Element columnDefinitions = createXAMLElement("Grid.ColumnDefinitions");
        for (String column : columns) {
            Element definition = createXAMLElement("ColumnDefinition");
            definition.setAttribute("Width", column);
            columnDefinitions.addContent(definition);
        }
        Element rowDefinitions = createXAMLElement("Grid.RowDefinitions");
        for (String row : rows) {
            Element definition = createXAMLElement("RowDefinition");
            definition.setAttribute("Height", row);
            rowDefinitions.addContent(definition);
        }
        rootElement.addContent(columnDefinitions);
        rootElement.addContent(rowDefinitions);
        return rootElement;
    }


    public Element createCanvas(String startColor, String endColor, double offcet){
        Element canvas = createXAMLElement("Canvas");
        Element canvasBackground = createXAMLElement("Canvas.Background");
        Element linearGradientBrush = createXAMLElement("LinearGradientBrush");
        Element gradientStopA = createXAMLElement("GradientStop");
        gradientStopA.setAttribute("Color", startColor);
        Element gradientStopB = createXAMLElement("GradientStop");
        gradientStopB.setAttribute("Color", endColor);
        gradientStopB.setAttribute("Offset", "" + offcet);

        linearGradientBrush.addContent(gradientStopA);
        linearGradientBrush.addContent(gradientStopB);
        canvasBackground.addContent(linearGradientBrush);
        canvas.addContent(canvasBackground);
        return canvas;
    }

    public Element createRectangle(int left, int top, String fill, int width, int height){
        Element rectangle = createXAMLElement("Rectangle");
        rectangle.setAttribute("Canvas.Left", ""+left);
        rectangle.setAttribute("Canvas.Top", ""+top);
        rectangle.setAttribute("Fill", fill);
        rectangle.setAttribute("Width", ""+width);
        rectangle.setAttribute("Height", ""+height);
        return rectangle;
    }

    public Element createPageElement(){
        Element pageElement = new Element("Page", getDefaultNamespace());
        pageElement.addNamespaceDeclaration(getXNamespace());
        pageElement.addNamespaceDeclaration(getNavigationNameSpace());
        pageElement.setAttribute("Title", "CompetenceView");

        return pageElement;
    }



    public Element createLabel(String text) {
        Element label = createXAMLElement("TextBlock");
        label.setAttribute("Text", text);
        label.setAttribute("TextBlock.FontSize", "10");
        return label;
    }


    public void createListOfButtons(List<String> names, Element gridElement) {
        Element stackPanelElement = createXAMLElement("StackPanel");
        for (String name : names) {
            Element textBlockElement = createXAMLElement("TextBlock");
            textBlockElement.setText(name);
            stackPanelElement.addContent(createBorderButton(textBlockElement));
        }
        Element scrollVieuwer = createScrollViewer();
        scrollVieuwer.addContent(stackPanelElement);
        gridElement.addContent(scrollVieuwer);
    }

    public Element createBorderButton(Element textBlockElement) {
        Element border = createXAMLElement("Border");
        border.setAttribute("Margin", "2");
        border.setAttribute("Padding", "2");
        border.setAttribute("Background", "White");
        border.setAttribute("BorderBrush", "LightGray");
        border.setAttribute("BorderThickness", "3,5,2,2");
        border.setAttribute("CornerRadius", "3");
        border.addContent(textBlockElement);
        return border;
    }

    public void addInterractionSilverlight(Element rootElement, String name){
        rootElement.setAttribute("Loaded", "onLoaded");
        rootElement.setAttribute("Name", name);
    }

    public Element createScrollViewer() {
        Element scrollViewer = createXAMLElement("ScrollViewer");
        scrollViewer.setAttribute("HorizontalScrollBarVisibility", "Disabled");
        scrollViewer.setAttribute("VerticalScrollBarVisibility", "Auto");
        scrollViewer.setAttribute("Grid.Row", "1");
        scrollViewer.setAttribute("Grid.Column", "0");
        return scrollViewer;

    }
    public Element createScrollViewer(int row, int column) {
        Element scrollViewer = createXAMLElement("ScrollViewer");
        scrollViewer.setAttribute("HorizontalScrollBarVisibility", "Disabled");
        scrollViewer.setAttribute("VerticalScrollBarVisibility", "Auto");
        scrollViewer.setAttribute("Grid.Row", ""+row);
        scrollViewer.setAttribute("Grid.Column", "" + column);
        return scrollViewer;

    }


    public Element createXAMLElement(String elementName) {
        Element element = new Element(elementName, getDefaultNamespace());
        element.addNamespaceDeclaration(getXNamespace());
        return element;
    }


    //------------------ file outputter en file reader ------------------------

    /**
     * Een xml file inlezen
     *
     * @param url
     * @return
     */

    public Document readXMLFromFile(String url) {
        File file = new File(url);
        SAXBuilder builder = new SAXBuilder();
        Document document = null;
        try {
            document = builder.build(file);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * Een xml file wegschrijven. een document en url als parameters
     *
     * @param document
     * @param uri
     */

    public void documentToFile(Document document, String uri) {
        try {
            FileWriter fileWriter = new FileWriter(new File(uri));
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, fileWriter);
        } catch (IOException e) {
            System.out.println("fout opgetreden in DocumentToFile method");
        }
    }


    public List<Element> getElementsFilterXpath(String filter, Document document) {
        XPathFactory xPathFactory = XPathFactory.instance();
        XPathExpression<Element> exp = xPathFactory.compile(filter, Filters.element());
        return exp.evaluate(document);
    }



    public static Namespace getDefaultNamespace() {
        return Namespace.getNamespace("http://schemas.microsoft.com/winfx/2006/xaml/presentation");
    }

    public static Namespace getXNamespace() {
        return Namespace.getNamespace("x", "http://schemas.microsoft.com/winfx/2006/xaml");
    }

    public static Namespace getNavigationNameSpace(){
        return Namespace.getNamespace("navigation", "clr-namespace:System.Windows.Controls;assembly=System.Windows.Controls.Navigation");
    }



}
