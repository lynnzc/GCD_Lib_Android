package app.cheng.gc.Data;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class BorrowedBookInfo {
    private String borrow_date;
    private String return_date;
    private String bookname;
    private String bookauthor;
    // 图书类型，一般为中文图书
    private String type;
    private String login_num;

    public String getBorrow_date() {
        return borrow_date;
    }

    public void setBorrow_date(String borrow_date) {
        this.borrow_date = borrow_date;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getBookauthor() {
        return bookauthor;
    }

    public void setBookauthor(String booauthor) {
        this.bookauthor = booauthor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogin_num() {
        return login_num;
    }

    public void setLogin_num(String login_num) {
        this.login_num = login_num;
    }

}

