import com.sun.org.apache.bcel.internal.generic.POP;

public class Test {
    public static void main(String[] args) {
        //发送
        SMTP mail = new SMTP();
        mail.Send(mail);
        //收
        POP3 mailpop = new POP3();
        mailpop.Get(mailpop);
    }
}
