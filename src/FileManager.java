import java.io.*;

public class FileManager
{
    String fileName = "GridData.txt";

    /**
     * Saves the provided data to the specified text file location in a comma-delimited format.
     *
     * @param gridData The data model containing the grid details taken from the UI screen.
     * @param filePath The file location we want the data saved to.
     */
    public void SaveDataToFile(GridData gridData, String filePath)
    {
        try
        {
            //Create a buffered writer to connect to our specified file and perform the writing operations.
            BufferedWriter buffer = new BufferedWriter(new FileWriter(filePath));

            //Write the headings data to the file before we write the grid data
            buffer.write("Name: ," + gridData.name);
            buffer.newLine();
            buffer.write("Date: ," + gridData.date);
            buffer.newLine();

            //Create a nested loop which will cycle through the 2D array. The outer loop will handle the columns (x-axis), the inner
            //loop will manage the rows (y-axis).
            for (int x = 0; x < gridData.data.length ; x++)
            {
                for (int y = 0; y < gridData.data[x].length; y++)
                {
                    //Write the current element, followed by a comma, on the current line of the file.
                    buffer.write(gridData.data[x][y] + ",");
                }
                //Start a new line once the current row is finished.
                buffer.newLine();
            }
            //Close the file buffer once writing is finished
            buffer.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public GridData ReadDataFromFile(String filePath)
    {
        //Create an empty data model to hold the file data
        GridData gridData = new GridData();
        //Create an empty 2D array to hold the grid details, this will be put into the grid data at the end.
        String[][] data = new String[5][10];

        //Try catch block is required in Java for any operations dealing with an external resource such as a file or database.
        try
        {
            //Create a buffered reader class object to read our file for us.
            BufferedReader buffer = new BufferedReader(new FileReader(filePath));

            //Sets the name and date fields. This is done by reading the line then performing the split command immediately on the line.
            //This normally would return an array of strings, but by adding the square brackets with an index number "[1]"
            //after the split command, it will then only return the index indicated and ignore the rest of the text.
            gridData.name = buffer.readLine().split(",")[1];
            gridData.date = buffer.readLine().split(",")[1];

            //Tracks how many lines we have currently read form the file which will also be the column number of the
            //2D array that the line will need to nbe written to.
            int counter = 0;
            //Variable to store each line once they are read in to the application
            String line;

            //Read the next line of text from the file, if it is not null (empty) proceed with the loop.
            while ((line = buffer.readLine()) != null)
            {
                //Split the line using the commas into separate elements
                String[] temp = line.split(",");

                //Cycle through the elements of the temp string array
                for (int i = 0; i < temp.length; i++)
                {
                    //Copy the text from the temp array at the current index to the column number and matching row index of the array.
                    data[counter][i] = temp[i];
                }
                //Increase the counter to indicate the current column has been written
                counter++;
            }

            //Close the file buffer once writing is finished
            buffer.close();
            //Pass the String array into the grid data to be stored in its data property.
            gridData.data = data;
            //Return the completed data model to the place this method was called from.
            return gridData;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            //Return null if an error occurs.
            return null;
        }
    }

    public void SaveDataToRaf(GridData gridData, String filePath)
    {
        //Try catch block is required in Java for any operations dealing with an external resource such as a file or database.
        try
        {
            //Create a random access file object to interact with the file.
            //It needs to be provided with a file name to write to and the file mode "rw" to allow writing.
            RandomAccessFile raf = new RandomAccessFile(filePath,"rw");

            //Write the header data before we cycle through the grid elements.
            raf.seek(0);
            raf.writeUTF(gridData.name);
            raf.seek(50);
            raf.writeUTF(gridData.date);

            //Tracks how many entries we have currently written to file
            int counter = 0;

            //Create a nested loop which will cycle through the 2D array. The outer loop will handle the columns (x-axis), the inner
            //loop will manage the rows (y-axis).
            for (int x = 0; x < gridData.data.length ; x++)
            {
                for (int y = 0; y < gridData.data[x].length; y++)
                {
                    //Check if the current element is empty or not
                    if (gridData.data[x][y].isEmpty())
                    {
                        //If it is empty, skip to the next loop.
                        continue;
                    }
                    //Calculates the starting index for the first entry. The multiplication section works out the space between each entry.
                    //The addition of 100 to the end is to allow for the header data so that we don;t overwrite it. This value will be
                    int pointer = counter * 40 + 100;
                    //Go to the file position specified by the pointer.
                    raf.seek(pointer);
                    //Write an integer value at this position.
                    raf.writeInt(x);
                    //Go to the next file position specified by the pointer plus the distance to the next value.
                    raf.seek(pointer + 5);
                    raf.writeInt(y);
                    //Go to the next file position specified by the pointer plus the distance to the next value.
                    raf.seek(pointer + 10);
                    raf.writeUTF(gridData.data[x][y]);
                    //Increase our counter to indicate an entry has been written.
                    counter++;
                }
            }

            //Closes the file connection and finalises the write process.
            raf.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public GridData ReadDataFromRaf(String filePath)
    {
        //create a new gridData class to hold out file contents
        GridData gridData = new GridData();

        //Create a new empty 2D string array to hold our data once it is read back in.
        String[][] data = new String[5][10];
        //Any operation to external resources in JAVA needs to be done in a try catch block.
        try
        {
            //Create a random access file object to interact with the file.
            //It needs to be provided with a file name to write to and the file mode "rw" to allow writing.
            RandomAccessFile raf = new RandomAccessFile(filePath,"r");

            //Read the header data from the file we cycle through the grid elements.
            raf.seek(0);
            gridData.name = raf.readUTF();
            raf.seek(50);
            gridData.date =  raf.readUTF();

            //Counter to track how many entries we have read back so far
            int counter = 0;

            //Check if the next pointer position will be larger than the total RAF file size.
            //If not, keep running the loop.
            while (counter * 40 + 100 < raf.length())
            {
                //Calculate the starting position of the next entity's entry
                int pointer = counter * 40 + 100;
                //Go to the raf file position specified by the pointer value
                raf.seek(pointer);
                //Read the integer value at this position and store it in an integer variable
                int xPos = raf.readInt();
                //Go to the raf file position specified by the pointer value plus the size of the first value
                raf.seek(pointer + 5);
                //Read the integer value at this position and store it in an integer variable
                int yPos = raf.readInt();
                //Go to the raf file position specified by the pointer value plus the size of the first 2 values
                raf.seek(pointer + 10);
                String text = raf.readUTF();
                //Use the retrieved values to put the text into the 2D array at the positions specified by the x and y positions.
                data[xPos][yPos] = text;
                //Increase the counter to confirm an entry has been successfully read back in.
                counter++;
            }
            //Closes the file connection and finalises the write process.
            raf.close();

            //Assign the 2D array into the data model
            gridData.data = data;
            //Return the data model back to where this method was called from.
            return gridData;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
