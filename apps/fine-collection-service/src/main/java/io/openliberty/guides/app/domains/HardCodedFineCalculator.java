package io.openliberty.guides.app.domains;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HardCodedFineCalculator implements FineCalculator
{
    public int CalculateFine(int violationInKmh)
    {
        int fine = 9; // default administration fee
        
        if (violationInKmh < 5)
        {
            fine += 18;
        }
        else if (violationInKmh >= 5 && violationInKmh < 10)
        {
            fine += 31;
        }
        else if (violationInKmh >= 10 && violationInKmh < 15)
        {
            fine += 64;
        }
        else if (violationInKmh >= 15 && violationInKmh < 20)
        {
            fine += 121;
        }
        else if (violationInKmh >= 20 && violationInKmh < 25)
        {
            fine += 174;
        }
        else if (violationInKmh >= 25 && violationInKmh < 30)
        {
            fine += 232;
        }
        else if (violationInKmh >= 25 && violationInKmh < 35)
        {
            fine += 297;
        }
        else if (violationInKmh == 35)
        {
            fine += 372;
        }
        else
        {
            // violation above 35 KMh will be determined by the prosecutor
            return 0;
        }

        return fine;
    }
}