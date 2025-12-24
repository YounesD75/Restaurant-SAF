package com.saf.client;

import com.saf.core1.ActorRef;
import com.saf.core1.router.RouterMessages;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/scaling")
public class ClientScalingController {

    private final ActorRef clientRouter;

    public ClientScalingController(@Qualifier("clientRouter") ActorRef clientRouter) {
        this.clientRouter = clientRouter;
    }

    @PostMapping("/client/up")
    public String scaleUp(@RequestParam(defaultValue = "1") int count) {
        clientRouter.tell(new RouterMessages.ScaleUp(count));
        return "Demande envoyée : Ajout de " + count + " workers client.";
    }

    @PostMapping("/client/down")
    public String scaleDown(@RequestParam(defaultValue = "1") int count) {
        clientRouter.tell(new RouterMessages.ScaleDown(count));
        return "Demande envoyée : Suppression de " + count + " workers client.";
    }
}
