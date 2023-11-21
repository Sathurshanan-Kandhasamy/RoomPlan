import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.LinkedList;

public class MainForm extends JFrame
{
    SpringLayout layout = new SpringLayout();
    FileManager file = new FileManager();
    //Creates a 2D array with 5 main elements each containing a sub-array of 10 JTextFields
    JTextField[][] textGrid = new JTextField[5][10];
    JButton btnSave,btnRaf;
    JButton btnSort;

    public MainForm()
    {
        setSize(400,400);
        setLocation(400,200);

        setLayout(layout);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Create a nested loop which will cycle through the 2D array. The outer loop will handle the columns (x-axis), the inner
        //loop will manage the rows (y-axis).
        for (int x = 0; x < textGrid.length; x++)
        {
            for (int y = 0; y < textGrid[x].length; y++)
            {
                //Determine the required X and Y padding for the current component being added.
                //The calculation used for each uses the following formula:
                //      {row/column spacing} * {row/column number} + {margin distance}
                int xPad = 65 * x + 25;
                int yPad= 25 * y +25;

                //Creates a JTextField for the specified element at the provided position.
                textGrid[x][y] = UIBuilderLibrary.BuildJTextFieldWithNorthWestAnchor(5,xPad,yPad,layout,this);
                add(textGrid[x][y]);
            }
        }

        btnSave = UIBuilderLibrary.BuildJButtonInlineBelow(80,25,"SAVE",10,null,layout,textGrid[4][9]);
        add(btnSave);

        btnRaf = UIBuilderLibrary.BuildJButtonInlineBelow(110,25,"Save to RAF",10,null,layout,textGrid[2][9]);
        add(btnRaf);

        btnSort = UIBuilderLibrary.BuildJButtonInlineBelow(80,25,"Sort",10,null,layout,textGrid[0][9]);
        add(btnSort);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //Make the user select where they want to save and get the location and file name selected.
                String filePath = SelectFilePath(".csv");

                GridData gridData = TransferDataToModel();
                //Pass the string 2D array to the file manager to be saved to the data file.
                file.SaveDataToFile(gridData, filePath);
            }
        });

        btnRaf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Make the user select where they want to save and get the location and file name selected.
                String filePath = SelectFilePath(".raf");
                GridData gridData  = TransferDataToModel();
                //Pass the string 2D array to the file manager to be saved to the data file.
                file.SaveDataToRaf(gridData, filePath);
            }
        });

        btnSort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PopulateSortForm();
            }
        });

        //Create a grid data model to store our file data prior to copying it to the text array
        GridData gridData = SelectAndLoadFileData();


        //If the file read returned data and didn't return null, put the data on screen.
        if (gridData != null)
        {
            //Somewhere here you will pass the header data from the model into the header text fields

            //Cycle through the text grid array
            for (int x = 0; x < textGrid.length; x++)
            {
                for (int y = 0; y < textGrid[x].length; y++)
                {
                    //Copy the text from the fileData array into the matching index position of the text grid array.
                    //of the string 2D array.
                    textGrid[x][y].setText(gridData.data[x][y]);
                }
            }
        }

        setVisible(true);
    }

    private void PopulateSortForm()
    {
        //Create a list to hold the text values and their counts
        LinkedList<TextDetails> textCountList = new LinkedList<>();

        //Cycle through the 2D array of fields
        for (int x = 0; x < textGrid.length; x++)
        {
            for (int y = 0; y < textGrid[x].length; y++)
            {
                //If the current field is empty, skip to the next loop cycle
                if (textGrid[x][y].getText().isEmpty())
                {
                    continue;
                }
                String word = textGrid[x][y].getText();
                //Check if the current text is already in the list
                int index = CheckListForItem(textCountList, word);
                //If not, add it
                if (index == -1)
                {
                    //Add the word to the list with a count of 1
                    textCountList.add(new TextDetails(word,1));
                }
                //If so, increase the count for that item
                else
                {
                    //Go to the index where the word was found an increase its count by 1
                    textCountList.get(index).count++;
                }
            }
        }

        //Sort the list - this requires the comparable interface to be applied to the TextDetails objects in the list.
        Collections.sort(textCountList);
        //Pass the list to the sort form and open it on screen
        new SortPage(textCountList);
    }

    private int CheckListForItem(LinkedList<TextDetails> textCountList, String text)
    {
        //Cycle through the current list of text values
        for (int i = 0; i < textCountList.size(); i++)
        {
            //Get the index of the current loop and check if the text value of that entry
            //matches the provided text parameter.
            if (textCountList.get(i).text.equalsIgnoreCase(text))
            {
                //Return the index of where the word was found.
                return i;
            }
        }
        //Return -1 which will be treated as a not found value.
        return -1;
    }

    private GridData TransferDataToModel() {
        //Create a new 2D String array the same size as the textfield array.
        String[][] textData = new String[5][10];

        //Create a nested loop which will cycle through the 2D array. The outer loop will handle the columns (x-axis), the inner
        //loop will manage the rows (y-axis).
        for (int x = 0; x < textGrid.length; x++)
        {
            for (int y = 0; y < textGrid[x].length; y++)
            {
                //Copy the text from the current element of the Text fields into the same index position
                //of the string 2D array.
                textData[x][y] = textGrid[x][y].getText();
            }
        }
        //Pass the text data and other properties into our data model.
        // they would be values retrieved from the headings of the screen.
        GridData gridData = new GridData("Sathurshanan","21-Nov-23",textData);
        return gridData;
    }

    private String SelectFilePath(String extension)
    {
        //Creates a file dialog (file explorer window) to allow the user to select where they want to save their file.
        FileDialog dialog = new FileDialog(this,"Select Save Location", FileDialog.SAVE);
        //Set the default file name for the save file. This can be changes by the user in the dialog.
        dialog.setFile("Untitled" + extension);
        //Make the file dialog visible on screen. This will lock the parent screen until the user is finished with the dialog.
        dialog.setVisible(true);

        //Get the slected file name provided by the user
        String fileName = dialog.getFile();
        //If the file name is empty or is just the extension type without any name before it. Return null.
        if (fileName == null || fileName.isEmpty() || fileName.equalsIgnoreCase(extension))
        {
            return null;
        }
        //If the filename extension has been changed and no longer matches the provided extension type.
        //NOTE: You might not always do this step if you want to allow the user to pick their own extension type.
        if (fileName.endsWith(extension) == false)
        {
            //Split the filename into sections
            String[] temp = fileName.split("\\.");
            //Take the first part fo the file name (the name section) and add the file extension back to the end of it.
            fileName = temp[0] + extension;
        }
        //Get the file directory that we chose to save the file in and add the file's name to the end of the path.
        String filePath = dialog.getDirectory() + fileName;
        //Return the full file path back to where the method was called.
        return filePath;
    }

    private GridData SelectAndLoadFileData()
    {
        //Creates a file dialog to allow the user to choose which file they want top open.
        FileDialog dialog = new FileDialog(this, "Select .csv or .raf file to Open.", FileDialog.LOAD);
        //Make the file dialog visible on screen. This will lock the parent screen until the user is finished with the dialog.
        dialog.setVisible(true);
        //Get the name of the file selected by the user,
        String fileName = dialog.getFile();
        //If the selected file is null or empty, meaning they did not select a file or pressed cancel.
        if (fileName == null || fileName.isEmpty())
        {
            //Post a message to the user and return null;
            JOptionPane.showMessageDialog(this,"No valid file selected. Opening Empty page.");
            return null;
        }
        //Create a model to hold our filedata
        GridData fileData;
        //Get the absolute file path of the file to be loaded
        String filePath = dialog.getDirectory() + fileName;
        //Check the file path extension. If the xtension is .csv or .raf, load the file with the correct method.
        //Otherwise, post an error message to the user.
        if (filePath.endsWith(".csv"))
        {
            //Read the file using the CSV reading method.
            fileData = file.ReadDataFromFile(filePath);
        }
        else if(filePath.endsWith(".raf"))
        {
            //Read the file using the RAF reading method.
            fileData = file.ReadDataFromRaf(filePath);
        }
        else
        {
            //Inform the user of the issue and set the fileData variable to null;
            JOptionPane.showMessageDialog(this,"The selected file was not a valid type.");
            fileData = null;
        }
        //Return the fileData contents.
        return fileData;
    }
}
