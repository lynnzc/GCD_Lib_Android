package app.cheng.gc.Data;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class BookInfo {
    //序号
    String serialnum;
    //书名
    String bookname;
    //作者
    String bookauthor;
    //出版者
    String publisher;
    //出版年
    String publishyear;
    //索取号
    String collectnum;
    //馆藏
    String booksize;
    //可借
    String canborrow;

    public String getSerialnum() {
        return serialnum;
    }

    public void setSerialnum(String serialnum) {
        this.serialnum = serialnum;
    }

    public String getBookname() {
        return this.bookname;
    }

    public String getBookauthor() {
        return this.bookauthor;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public String getPublishyear() {
        return this.publishyear;
    }

    public String getCollectnum() {
        return this.collectnum;
    }

    public String getBooksize() {
        return this.booksize;
    }

    public String getCanborrow() {
        return this.canborrow;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public void setBookauthor(String bookauthor) {
        this.bookauthor = bookauthor;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishyear(String publishyear) {
        this.publishyear = publishyear;
    }

    public void setCollectnum(String collectnum) {
        this.collectnum = collectnum;
    }

    public void setBooksize(String booksize) {
        this.booksize = booksize;
    }

    public void setCanborrow(String canborrow) {
        this.canborrow = canborrow;
    }

    @Override
    public String toString() {
        return bookname + "\n" + bookauthor + "\n" + publisher + "\n"
                + publishyear + "\n" + collectnum + "\n" + booksize + "\n" +
                canborrow;
    }
}

