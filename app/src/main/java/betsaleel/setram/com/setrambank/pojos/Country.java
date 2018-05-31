package betsaleel.setram.com.setrambank.pojos;

/**
 * Created by hornellama on 30/12/2017.
 */

public class Country {
    private String name;
    private String language;
    private int image_flags;

    public Country(String name,String language,int image_flags){
        this.name=name;
        this.language=language;
        this.image_flags=image_flags;
    }

    public int getImage_flags() {
        return image_flags;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public void setImage_flags(int image_flags) {
        this.image_flags = image_flags;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setName(String name) {
        this.name = name;
    }
}
