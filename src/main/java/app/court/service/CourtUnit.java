package app.court.service;

import app.court.model.Court;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
public class CourtUnit implements CommandLineRunner {

    private final CourtService courtService;

    @Autowired
    public CourtUnit(CourtService courtService) {
        this.courtService = courtService;
    }

    @Override
    public void run(String... args) throws Exception {
        /*Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();
        Court court2 = Court.builder()
                .name("Court1")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://s42493.pcdn.co/wp-content/uploads/2023/07/courts-scaled.jpg")
                .build();
        Court court3 = Court.builder()
                .name("Court2")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQAcssBEWVhf4Xqw73CNCqE_atRazZbSxVxeg&s")
                .build();
        Court court4 = Court.builder()
                .name("Court3")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://i0.wp.com/rt42.org/wp-content/uploads/2022/01/what-is-RT-scaled.jpg?fit=750%2C500&ssl=1")
                .build();
        Court court5 = Court.builder()
                .name("Court4")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQVbWgMlrJt0nbZE4Lte54zSvUaQz8TUqp_4w&s")
                .build();
        courtService.addCourt(court);
        courtService.addCourt(court2);
        courtService.addCourt(court3);
        courtService.addCourt(court4);
        courtService.addCourt(court5);*/
    }
}
