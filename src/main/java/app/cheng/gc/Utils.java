package app.cheng.gc;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import app.cheng.gc.Data.BookInfo;
import app.cheng.gc.Data.BorrowedBookInfo;
import app.cheng.gc.Data.SearchBookInfo;
import app.cheng.gc.Data.StudentInfo;

/**
 * Created by lynnlyf on 2015/2/2.
 */

public class Utils {
    private static String __VIEWSTATE;
    private static String __EVENTVALIDATION;
    private static int searchNum = 0;
    private static int borrowedMax = 0;

    public static int getBorrowedMax() {
        return borrowedMax;
    }

    public static void setBorrowedMax(int borrowedMax) {
        Utils.borrowedMax = borrowedMax;
    }

    public static int getSearchNum() {
        return searchNum;
    } //查找总数

    public static String get__VIEWSTATE() {
        return __VIEWSTATE;
    }

    public static String get__EVENTVALIDATION() {
        return __EVENTVALIDATION;
    }

    public static StudentInfo getInfo(String html) {
        if(html != null) {
            Document doc = Jsoup.parse(html);
            Elements eles = doc.getElementById("userInfoContent").getElementsByClass("infoline");
            StudentInfo s_info = new StudentInfo();
            int count = 0;
            for (Element e : eles) {
                switch (count) {
                    case 0:
                        s_info.setCardNumber(e.getElementsByClass("inforight").text());
                        break;
                    case 1:
                        s_info.setName(e.getElementsByClass("inforight").text());
                        break;
                    case 2:
                        s_info.setType(e.getElementsByClass("inforight").text());
                        break;
                    case 3:
                        s_info.setCollege(e.getElementsByClass("inforight").text());
                        break;
                    case 4:
                        s_info.setBorrowState(e.getElementsByClass("inforight").text());
                        break;
                    case 5:
                        s_info.setPhoneNumber(e.getElementsByClass("inforight").text());
                        break;
                    case 6:
                        s_info.setEmailAddress(e.getElementsByClass("inforight").text());
                        break;
                    default:
                        break;
                }
                count++;
            }

            return s_info;
        }
        else {
            System.out.println("空html"); //测试
            return null;
        }
    }

    public static void getViewState(String html) {
        Document doc = Jsoup.parse(html);
        __VIEWSTATE = doc.getElementById("__VIEWSTATE").attr("value").toString();
        __EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").attr("value").toString();
    }

    public static List<BorrowedBookInfo> getBorrowed(String html) {
        List<BorrowedBookInfo> borrowedBookList = new ArrayList<BorrowedBookInfo>();
        Document doc = Jsoup.parse(html);
        Elements eles = doc.getElementById("borrowedcontent").getElementsByTag("tr");

        int count = 1; //skip the first tr
        for(Element e: eles) {
            if(count > 1) {
                Elements td_eles = e.getElementsByTag("td");
                BorrowedBookInfo borrowedInfo = new BorrowedBookInfo();
                for(int i = 0 ; i < td_eles.size() ; i++) {
                    switch(i) {
                        case 0:
                            break;
                        case 1:
                            borrowedInfo.setReturn_date(td_eles.get(i).text());
                            break;
                        case 2:
                            borrowedInfo.setBookauthor(td_eles.get(i).text());
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            borrowedInfo.setBorrow_date(td_eles.get(i).text());
                            break;
                        default:
                            break;
                    }
                }
                borrowedBookList.add(borrowedInfo);
            }
            count++;
        }
        return borrowedBookList;
    }

    public static List<BorrowedBookInfo> getHistory(String html) {
        Document doc = Jsoup.parse(html);

        borrowedMax = doc.getElementById("ctl00_cpRight_Pagination1_gotoddl2").
                getElementsByTag("option").size();

        System.out.println(borrowedMax + " /borrowedMax");

        Utils.setBorrowedMax(borrowedMax);

        Elements eles = doc.getElementsByClass("tb");
        List<BorrowedBookInfo> list_historybook = new ArrayList<BorrowedBookInfo>();

        for(Element e: eles) {
            Elements list_es  = e.getElementsByTag("tr");
            for(int i = 1 ; i < list_es.size() ; i++) {
                //i skip the first list
                Elements item_es = list_es.get(i).getElementsByTag("td");
                BorrowedBookInfo bed_bookinfo = new BorrowedBookInfo();
                for(int j = 0 ; j < item_es.size() ; j++) {
                    switch(j) {
                        case 0:
                            bed_bookinfo.setBorrow_date(item_es.get(j).text());
                            break;
                        case 1:
                            bed_bookinfo.setReturn_date(item_es.get(j).text());
                            break;
                        case 2:
                            bed_bookinfo.setBookauthor(item_es.get(j).text());
                            break;
                        case 3:
                            bed_bookinfo.setType(item_es.get(j).text());
                            break;
                        case 4:
                            bed_bookinfo.setLogin_num(item_es.get(j).text());
                            break;
                        default:
                            break;
                    }
                }
                list_historybook.add(bed_bookinfo);
            }
        }
        return list_historybook;
    }

    public static List<BorrowedBookInfo> getBookmark(String html) {
        Document doc = Jsoup.parse(html);
        borrowedMax = doc.getElementById("ctl00_cpRight_Pagination2_gotoddl2").
                getElementsByTag("option").size();
        Utils.setBorrowedMax(borrowedMax);

        Elements eles = doc.getElementsByClass("tb");
        List<BorrowedBookInfo> list_bookmark = new ArrayList<BorrowedBookInfo>();

        for (Element e : eles) {
            Elements list_es = e.getElementsByTag("tr");
            for (int i = 1; i < list_es.size(); i++) {
                //跳过第一行属性
                Elements item_es = list_es.get(i).getElementsByTag("td");
                BorrowedBookInfo bookmark_info = new BorrowedBookInfo();
                for (int j = 0; j < item_es.size(); j++) {
                    switch (j) {
                        case 0:
                            bookmark_info.setLogin_num(item_es.get(j).text());
                            break;
                        case 1:
                            bookmark_info.setBookname(item_es.get(j).text());
                            break;
                        case 2:
                            bookmark_info.setBorrow_date(item_es.get(j).text());
                            break;
                        case 3:
                            break;
                        default:
                            break;
                    }
                }
                list_bookmark.add(bookmark_info);
            }

        }
        return list_bookmark;
    }

    public static List<BookInfo> getSearch(String html) {
        List<BookInfo> bookInfoList = new ArrayList<BookInfo>();
        Document doc = Jsoup.parse(html);

        searchNum = Integer.parseInt(doc.getElementById
                ("ctl00_ContentPlaceHolder1_countlbl").text()); //查找的总数

        System.out.println("searchNum: " + searchNum); //测试

        if(searchNum == 0) {
            return null;
        }

        Elements eles = doc.getElementsByClass("tb").get(0).getElementsByTag("tr");

        for(int i = 1 ; i < eles.size() ; i++) {
            //i skip the first list
            BookInfo bookinfo = new BookInfo();
            Elements td_eles = eles.get(i).getElementsByTag("td");
            for(int j = 0 ; j < td_eles.size() ; j++) {
                switch(j) {
                    case 0:
                        bookinfo.setSerialnum(td_eles.get(j).
                                getElementsByTag("input").attr("value"));
                        break;
                    case 1:
                        bookinfo.setBookname(td_eles.get(j).text().trim());
                        break;
                    case 2:
                        bookinfo.setBookauthor(td_eles.get(j).text().trim());
                        break;
                    case 3:
                        bookinfo.setPublisher(td_eles.get(j).text().trim());
                        break;
                    case 4:
                        bookinfo.setPublishyear(td_eles.get(j).text().trim());
                        break;
                    case 5:
                        bookinfo.setCollectnum(td_eles.get(j).text().trim());
                        break;
                    case 6:
                        bookinfo.setBooksize(td_eles.get(j).text().trim());
                        break;
                    case 7:
                        bookinfo.setCanborrow(td_eles.get(j).text().trim());
                        break;
                    default:
                        break;
                }
            }
            bookInfoList.add(bookinfo);
        }
        return bookInfoList;
    }

    //a book's borrowed detail
    public static List<SearchBookInfo> getSearchBookInfo(String html) {
        List<SearchBookInfo> sbookinfoList = new ArrayList<SearchBookInfo>();
        Document doc = Jsoup.parse(html);
        System.out.println(html + "/html");
        Elements eles = doc.getElementById("bardiv").getElementsByTag("tr");

        for(int i = 1 ; i < eles.size() ; i++) {
            Elements td_eles = eles.get(i).getElementsByTag("td");
            SearchBookInfo searchBookinfo = new SearchBookInfo();
            for(int j = 0 ; j < td_eles.size() ; j++) {
                switch(j) {
                    case 0:
                        searchBookinfo.setAddress(td_eles.get(j).text().trim());
                        break;
                    case 1:
                        searchBookinfo.setFetchnumber(td_eles.get(j).text().trim());
                        break;
                    case 5:
                        searchBookinfo.setIsLent(td_eles.get(j).text().trim());
                        System.out.println(searchBookinfo.IsLent());
                        break;
                    default:
                        break;
                }
            }
            sbookinfoList.add(searchBookinfo);
        }
        return sbookinfoList;
    }
}
