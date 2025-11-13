package com.saf.client;
import java.util.Scanner;
import com.saf.core1.*;
import com.saf.core1.*;

public class ActorComment implements Actor{
    private String comment;

    public ActorComment(String comment) {
        this.comment = comment;
    }
    public ActorComment() {
    }

    public void review() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter comment: ");
        setComment(input.nextLine());
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public static void main(String[] args) {

        ActorComment commentaire = new ActorComment();
        commentaire.review();
        System.out.println(commentaire.getComment());

    }

}
