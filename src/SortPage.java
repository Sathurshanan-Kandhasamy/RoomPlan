import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

public class SortPage extends JFrame
{
    SpringLayout layout = new SpringLayout();
    LinkedList<TextDetails> textDetailsList;
    JLabel[][] textArray;

    public SortPage(LinkedList<TextDetails> textDetails)
    {
        //Calculate the height for the window.
        int windowHeight = textDetails.size() * 25 + 65;
        setSize(200,windowHeight);

        setLocation(200,200);
        setLayout(layout);

        //Assign the text details passed into the form into the variable used for holding them.
        textDetailsList = textDetails;
        //Set the size of the array to match the number of entries passed over in the text count list.
        textArray = new JLabel[2][textDetails.size()];

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //Clears the form from memory. This is not too much of an issue in small projects but is useful in bigger ones to keep memory
                //usage low.
                dispose();
            }
        });

        SetupTextDataOnScreen();

        setVisible(true);
    }

    private void SetupTextDataOnScreen()
    {
        for (int x = 0; x < textArray.length; x++)
        {
            for (int y = 0; y < textArray[x].length; y++)
            {
                int xPad = 65 * x + 25;
                int yPad= 25 * y +25;

                textArray[x][y] = UIBuilderLibrary.BuildJLabelWithNorthWestAnchor("X",xPad,yPad,layout,this);
                add(textArray[x][y]);
            }
        }

        for (int i = 0; i < textDetailsList.size(); i++)
        {
            //Get the text from the current text list entry.
            String text = textDetailsList.get(i).text;
            //Get the count value from the current text list entry and convert it ot a string.
            String count = "" + textDetailsList.get(i).count;
            //Add the 2 values to their columns in the current textArray row.
            textArray[0][i].setText(text);
            textArray[1][i].setText(count);
        }
    }


}
