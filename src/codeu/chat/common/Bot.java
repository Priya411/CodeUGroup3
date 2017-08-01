package codeu.chat.common;

import codeu.chat.util.Uuid;

public abstract class Bot {

    public String reactTo(String text, Uuid sender)
    {
        return null;
    }
    abstract String onAdd();  // what bot does when it is first added
    public String getName()
    {
        return this.getClass().getName();
    }


}
