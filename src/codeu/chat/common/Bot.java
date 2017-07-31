package codeu.chat.common;

public abstract class Bot {

    abstract String reactTo(String command, String[] args);
    abstract String onAdd();  // what bot does when it is first added
    public String getName()
    {
        return this.getClass().getName();
    }


}
