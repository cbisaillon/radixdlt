package org.radix.network2.addressbook;

import org.radix.database.DatabaseEnvironment;
import org.radix.events.Events;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.radixdlt.serialization.Serialization;

import java.util.Objects;

/**
 * Guice configuration for {@link AddressBook}.
 */
final class AddressBookModule extends AbstractModule {

	private final DatabaseEnvironment dbEnv;

	AddressBookModule(DatabaseEnvironment dbEnv) {
		// Nothing
		this.dbEnv = Objects.requireNonNull(dbEnv);
	}

	@Override
	protected void configure() {
		// The main target
		bind(AddressBook.class).to(AddressBookImpl.class);

		// AddressBookImpl dependencies
		// See addressBookePersistenceProvider
		bind(Events.class).toProvider(Events::getInstance);

		// AddressBookPersistence dependencies
		bind(Serialization.class).toProvider(Serialization::getDefault);

		// Bind DatabaseEnvironment to global instance
		// This is temporary as we're in the progress of untangling the dependency and DI mess
		bind(DatabaseEnvironment.class).toInstance(dbEnv);
	}

	@Provides
	@Singleton
	PeerPersistence addressBookPersistenceProvider(Serialization serialization, DatabaseEnvironment dbEnv) {
		AddressBookPersistence persistence = new AddressBookPersistence(serialization, dbEnv);
		persistence.start();
		return persistence;
	}
}
