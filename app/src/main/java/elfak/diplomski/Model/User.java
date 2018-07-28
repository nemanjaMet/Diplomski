package elfak.diplomski.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Neca on 25.10.2017..
 */

public class User extends RealmObject {
    @PrimaryKey
    @Required  // The @Required annotation can be used to tell Realm to disallow null values in a field, making it required rather than optional
    private String username;
    private String password;
    private String name;
    private String last_name;
    private String phone_number;

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setLastname(String last_name)
    {
        this.last_name = last_name;
    }

    public String getLastname()
    {
        return this.last_name;
    }

    public void setPhone_number(String phone_number)
    {
        this.phone_number = phone_number;
    }

    public String getPhone_number()
    {
        return this.phone_number;
    }
}
