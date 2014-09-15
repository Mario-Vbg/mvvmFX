package de.saxsys.mvvmfx.contacts.ui.addressform;

import static de.saxsys.mvvmfx.contacts.ui.addressform.AddressFormViewModel.NOTHING_SELECTED_MARKER;
import static de.saxsys.mvvmfx.contacts.ui.addressform.AddressFormViewModel.SUBDIVISION_LABEL_KEY;
import static eu.lestard.assertj.javafx.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import de.saxsys.javafx.test.JfxRunner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.saxsys.mvvmfx.contacts.model.Country;
import de.saxsys.mvvmfx.contacts.model.CountrySelector;
import org.junit.runner.RunWith;

@RunWith(JfxRunner.class)
public class AddressFormViewModelTest {
	private AddressFormViewModel viewModel;
	
	private CountrySelector countrySelector;
	private Country germany;
	private Country austria;

	@Before
	public void setup(){
		// sadly the ResourceBundle.getString method is final so we can't use mockito
		ResourceBundle resourceBundle = new ListResourceBundle() {  
			@Override
			protected Object[][] getContents() {
				return new Object[][] {
						{SUBDIVISION_LABEL_KEY, "default_subdivision_label"}
				};
			}
		};
		countrySelector = new CountrySelector();
		germany = new Country("Deutschland", "DE");
		austria = new Country("Österreich", "AT");

		viewModel = new AddressFormViewModel();
		viewModel.resourceBundle = resourceBundle;
		viewModel.countrySelector = countrySelector;
	}

	@Test
	@Ignore
	public void testCountryAndFederalStateLists() throws Exception{

		CompletableFuture<Boolean> blocker = new CompletableFuture<>();

		countrySelector.inProgressProperty().addListener((obs, oldV, newV)->{
			if(!newV){
				blocker.complete(true);
			}
		});
		
		Platform.runLater(viewModel::init);
		
		blocker.get(1, TimeUnit.SECONDS);
		
		assertThat(viewModel.countriesList()).hasSize(4).contains(NOTHING_SELECTED_MARKER,"Austria", "Germany", "Switzerland");
		assertThat(viewModel.countriesList().get(0)).isEqualTo(NOTHING_SELECTED_MARKER);
		assertThat(viewModel.subdivisionsList()).hasSize(1).contains(NOTHING_SELECTED_MARKER);
		
		assertThat(viewModel.selectedCountryProperty()).hasValue(NOTHING_SELECTED_MARKER);
		assertThat(viewModel.selectedSubdivisionProperty()).hasValue(NOTHING_SELECTED_MARKER);
		
		assertThat(viewModel.subdivisionLabel()).hasValue("default_subdivision_label");
		
		
		viewModel.selectedCountryProperty().set("Germany");
		
		assertThat(viewModel.subdivisionsList()).hasSize(17).contains(NOTHING_SELECTED_MARKER, "Sachsen", "Berlin",
				"Bayern"); // test sample
		assertThat(viewModel.subdivisionsList().get(0)).isEqualTo(NOTHING_SELECTED_MARKER);
		assertThat(viewModel.selectedSubdivisionProperty()).hasValue(NOTHING_SELECTED_MARKER);
		assertThat(viewModel.subdivisionLabel()).hasValue("State");
		
		viewModel.selectedSubdivisionProperty().set("Sachsen");
		
		
		viewModel.selectedCountryProperty().set("Austria");
		
		assertThat(viewModel.selectedSubdivisionProperty()).hasValue(NOTHING_SELECTED_MARKER);
		assertThat(viewModel.subdivisionsList()).hasSize(10).contains(NOTHING_SELECTED_MARKER, "Wien", "Tirol", "Salzburg");
		assertThat(viewModel.subdivisionsList().get(0)).isEqualTo(NOTHING_SELECTED_MARKER);
		assertThat(viewModel.subdivisionLabel()).hasValue("State");
		
		viewModel.selectedSubdivisionProperty().set("Wien");
		
		
		viewModel.selectedCountryProperty().set(NOTHING_SELECTED_MARKER);
		assertThat(viewModel.selectedSubdivisionProperty()).hasValue(NOTHING_SELECTED_MARKER);
	
		assertThat(viewModel.subdivisionsList()).hasSize(1).contains(NOTHING_SELECTED_MARKER);
		assertThat(viewModel.subdivisionLabel()).hasValue("default_subdivision_label");
	}

	@Test
	public void testCreateListWithNothingSelectedMarker(){
		ObservableList<String> sourceList = FXCollections.observableArrayList();

		ObservableList<String> target = AddressFormViewModel
				.createListWithNothingSelectedMarker(sourceList);
		
		assertThat(target).hasSize(1).contains(NOTHING_SELECTED_MARKER);
		
		sourceList.add("test");
		assertThat(target).hasSize(2).containsExactly(NOTHING_SELECTED_MARKER, "test");
		
		sourceList.add("temp");
		assertThat(target).hasSize(3).containsExactly(NOTHING_SELECTED_MARKER, "test", "temp");
		
		sourceList.remove("test");
		assertThat(target).hasSize(2).containsExactly(NOTHING_SELECTED_MARKER, "temp");
		
		
		sourceList.clear();
		assertThat(target).hasSize(1).contains(NOTHING_SELECTED_MARKER);
	}
	
}
