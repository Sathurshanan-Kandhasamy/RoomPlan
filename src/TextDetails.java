public class TextDetails implements Comparable
{
    public String text;
    public int count;

    public TextDetails() {
    }

    public TextDetails(String t, int c)
    {
        text = t;
        count = c;
    }

    @Override
    public int compareTo(Object other)
    {
        //Convert and store the other object in a variable as its original type.
        TextDetails otherText = (TextDetails)other;
        //Compare the text of the other object against the text of this object and return the result.
        return text.compareTo(otherText.text);
    }
}
