package bekya.admin.Model;

/**
 * Created by ahmed on 1/16/2018.
 */

public class Sender {
   public String to ;
   public Notification notification ;

   public Sender(String to, Notification notification) {
      this.to = to;
      this.notification = notification;
   }

   public Sender() {
   }

   public Sender(String to) {
      this.to = to;
   }
   public Notification getNotification()
   {
      return notification;
   }
   public void setNotification (Notification notification){
      this.notification=notification;
   }
}
