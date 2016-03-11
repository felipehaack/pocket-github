package sp.br.concretesolution;

import android.app.Application;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ApplicationTestCase;

import org.junit.Test;

public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {

        super(Application.class);
    }
}