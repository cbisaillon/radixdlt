package org.radix.integration.stack;

import com.google.common.collect.ImmutableMap;
import com.radixdlt.common.Pair;
import com.radixdlt.utils.UInt384;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.radix.atoms.PreparedAtom;
import com.radixdlt.atomos.RadixAddress;
import com.radixdlt.atommodel.tokens.TokenDefinitionParticle.TokenTransition;
import com.radixdlt.atommodel.tokens.TransferrableTokensParticle;
import com.radixdlt.atommodel.tokens.TokenPermission;
import com.radixdlt.atommodel.tokens.Tokens;
import org.radix.atoms.Atom;
import org.radix.atoms.AtomDependencyNotFoundException;
import org.radix.atoms.AtomDiscoveryRequest;
import org.radix.atoms.AtomStore;
import com.radixdlt.constraintmachine.CMAtom;
import com.radixdlt.atoms.ParticleGroup;
import com.radixdlt.atomos.RRI;
import com.radixdlt.atoms.Spin;
import org.radix.atoms.particles.conflict.ParticleConflictException;
import com.radixdlt.utils.Offset;
import org.radix.time.NtpService;
import com.radixdlt.universe.Universe;
import org.radix.validation.ConstraintMachineValidationException;
import com.radixdlt.crypto.ECKeyPair;
import org.radix.discovery.DiscoveryRequest.Action;
import org.radix.integration.RadixTestWithStores;
import org.radix.modules.Modules;
import org.radix.properties.RuntimeProperties;
import org.radix.time.Time;
import com.radixdlt.utils.UInt256;
import org.radix.validation.ValidationHandler;

import java.io.File;

public class TokenTransferValidationTest extends RadixTestWithStores {
	private static final UInt256 HUNDRED = UInt256.TEN.pow(2);
	private static final UInt256 MILLION = UInt256.TEN.pow(6);
	private ECKeyPair 	identity;
	private RadixAddress universeAddress;
	private Universe universe;

	@Before
	public void createIdentity() throws Exception {
		String universeKeyPath = Modules.get(RuntimeProperties.class).get("universe.key.path", "universe.key");
		identity = ECKeyPair.fromFile(new File(universeKeyPath), true);
		universe = Modules.get(Universe.class);
		universeAddress = RadixAddress.from(universe, identity.getPublicKey());
	}

	@Test
	public void testSimpleTransfer() throws Exception {
		AtomDiscoveryRequest request = new AtomDiscoveryRequest(TransferrableTokensParticle.class, Action.DISCOVER_AND_DELIVER);
		request.setDestination(universeAddress.getUID());
		Modules.get(AtomStore.class).discovery(request);

		Atom atom = new Atom(Time.currentTimestamp());

		TransferrableTokensParticle consumer = request.getDelivered().get(0).particles(TransferrableTokensParticle.class, Spin.UP)
			.findFirst().get();

		ParticleGroup.ParticleGroupBuilder group = ParticleGroup.builder();
		group.addParticle(consumer, Spin.DOWN);
		RadixAddress addr = RadixAddress.from(universe, new ECKeyPair().getPublicKey());
		group.addParticle(new TransferrableTokensParticle(
			addr, consumer.getAmount().subtract(HUNDRED), UInt256.ONE, consumer.getTokDefRef(),
			currentPlanckTime(),
			consumer.getTokenPermissions()
		), Spin.UP);
		group.addParticle(new TransferrableTokensParticle(
			universeAddress, HUNDRED, UInt256.ONE, consumer.getTokDefRef(),
			currentPlanckTime(),
			consumer.getTokenPermissions()
		), Spin.UP);
		atom.addParticleGroup(group.build());
		atom.sign(identity);

		Pair<CMAtom, UInt384> result = Modules.get(ValidationHandler.class).validate(atom);
		Modules.get(ValidationHandler.class).stateCheck(result.getFirst());
	}

	@Test
	public void testTooMuchConsumed() throws Exception {
		AtomDiscoveryRequest request = new AtomDiscoveryRequest(TransferrableTokensParticle.class, Action.DISCOVER_AND_DELIVER);
		request.setDestination(universeAddress.getUID());
		Modules.get(AtomStore.class).discovery(request);

		Atom atom = new Atom(Time.currentTimestamp());

		ParticleGroup.ParticleGroupBuilder group = ParticleGroup.builder();
		TransferrableTokensParticle consumer = request.getDelivered().get(0).particles(TransferrableTokensParticle.class, Spin.UP).findFirst().get();
		group.addParticle(consumer, Spin.DOWN);
		RadixAddress addr = RadixAddress.from(universe, new ECKeyPair().getPublicKey());
		group.addParticle(new TransferrableTokensParticle(
			addr, consumer.getAmount().subtract(HUNDRED), UInt256.ONE, consumer.getTokDefRef(),
			currentPlanckTime(),
			consumer.getTokenPermissions()
		), Spin.UP);
		group.addParticle(new TransferrableTokensParticle(
			universeAddress, HUNDRED.multiply(UInt256.TWO), UInt256.ONE, consumer.getTokDefRef(),
			currentPlanckTime(),
			consumer.getTokenPermissions()
		), Spin.UP);
		atom.addParticleGroup(group.build());
		atom.sign(identity);

		Assertions.assertThatThrownBy(() -> Modules.get(ValidationHandler.class).validate(atom))
			.isInstanceOf(ConstraintMachineValidationException.class)
			.hasMessageContaining("100 unsatisfied");
	}

	@Test
	public void testTooLittleConsumed() throws Exception {
		AtomDiscoveryRequest request = new AtomDiscoveryRequest(TransferrableTokensParticle.class, Action.DISCOVER_AND_DELIVER);
		request.setDestination(universeAddress.getUID());
		Modules.get(AtomStore.class).discovery(request);

		Atom atom = new Atom(Time.currentTimestamp());

		ParticleGroup.ParticleGroupBuilder group = ParticleGroup.builder();
		TransferrableTokensParticle consumer = request.getDelivered().get(0).particles(TransferrableTokensParticle.class, Spin.UP).findFirst().get();
		group.addParticle(consumer, Spin.DOWN);
		RadixAddress addr = RadixAddress.from(universe, new ECKeyPair().getPublicKey());
		group.addParticle(new TransferrableTokensParticle(
			addr, consumer.getAmount().subtract(HUNDRED), UInt256.ONE, consumer.getTokDefRef(),
			currentPlanckTime(),
			consumer.getTokenPermissions()
		), Spin.UP);
		atom.addParticleGroup(group.build());
		atom.sign(identity);

		Assertions.assertThatThrownBy(() -> Modules.get(ValidationHandler.class).validate(atom))
			.isInstanceOf(ConstraintMachineValidationException.class)
			.hasMessageContaining("100 unspent");
	}

	private static long currentPlanckTime() {
		return Modules.get(Universe.class).toPlanck(Modules.get(NtpService.class).getUTCTimeMS(), Offset.NONE);
	}

	@Test
	public void testConsumableDoesNotExist() throws Exception {
		Atom atom = new Atom(Time.currentTimestamp());
		ParticleGroup.ParticleGroupBuilder group = ParticleGroup.builder();
		ImmutableMap<TokenTransition, TokenPermission> permissions =
			ImmutableMap.of(TokenTransition.MINT, TokenPermission.TOKEN_OWNER_ONLY, TokenTransition.BURN, TokenPermission.NONE);

		TransferrableTokensParticle fakeConsumable = new TransferrableTokensParticle(universeAddress,
			MILLION,
			UInt256.ONE,
			RRI.of(universeAddress, Tokens.getNativeTokenShortCode()),
			currentPlanckTime(),
			permissions
		);
		group.addParticle(fakeConsumable, Spin.DOWN);
		group.addParticle(new TransferrableTokensParticle(
			RadixAddress.from(universe, new ECKeyPair().getPublicKey()),
			fakeConsumable.getAmount(),
			UInt256.ONE,
			fakeConsumable.getTokDefRef(),
			currentPlanckTime(),
			permissions
		), Spin.UP);
		atom.addParticleGroup(group.build());
		atom.sign(identity);

		Pair<CMAtom, UInt384> result = Modules.get(ValidationHandler.class).validate(atom);

		Assertions.assertThatThrownBy(() -> Modules.get(ValidationHandler.class).stateCheck(result.getFirst()))
			.isInstanceOf(AtomDependencyNotFoundException.class);
	}

	@Test
	public void testConsumerDoubleSpend() throws Exception {
		AtomDiscoveryRequest request = new AtomDiscoveryRequest(TransferrableTokensParticle.class, Action.DISCOVER_AND_DELIVER);
		request.setDestination(universeAddress.getUID());
		Modules.get(AtomStore.class).discovery(request);

		TransferrableTokensParticle consumable = request.getDelivered().get(0).particles(TransferrableTokensParticle.class, Spin.UP).findFirst().get();
		UInt256 amount = consumable.getAmount();

		ParticleGroup.ParticleGroupBuilder firstGroup = ParticleGroup.builder();
		firstGroup.addParticle(consumable, Spin.DOWN);
		RadixAddress addr1 = RadixAddress.from(universe, new ECKeyPair().getPublicKey());
		firstGroup.addParticle(new TransferrableTokensParticle(
			addr1, amount.subtract(HUNDRED), UInt256.ONE, consumable.getTokDefRef(),
			currentPlanckTime(),
			consumable.getTokenPermissions()
		), Spin.UP);
		firstGroup.addParticle(new TransferrableTokensParticle(
			universeAddress, HUNDRED, UInt256.ONE, consumable.getTokDefRef(),
			currentPlanckTime(),
			consumable.getTokenPermissions()
		), Spin.UP);

		Atom firstAtom = new Atom(Time.currentTimestamp());
		firstAtom.addParticleGroup(firstGroup.build());
		addTemporalVertex(firstAtom); // Can't store atom without vertex from this node
		firstAtom.sign(identity);
		Pair<CMAtom, UInt384> result = Modules.get(ValidationHandler.class).validate(firstAtom);
		Modules.get(ValidationHandler.class).stateCheck(result.getFirst());
		PreparedAtom preparedAtom = new PreparedAtom(result.getFirst(), result.getSecond());
		Modules.get(AtomStore.class).storeAtom(preparedAtom);

		ParticleGroup.ParticleGroupBuilder secondGroup = ParticleGroup.builder();
		Atom secondAtom = new Atom(Time.currentTimestamp());
		secondGroup.addParticle(consumable, Spin.DOWN);
		RadixAddress addr2 = RadixAddress.from(universe, new ECKeyPair().getPublicKey());
		secondGroup.addParticle(new TransferrableTokensParticle(
			addr2, amount.subtract(HUNDRED), UInt256.ONE, consumable.getTokDefRef(),
			currentPlanckTime(),
			consumable.getTokenPermissions()
		), Spin.UP);
		secondGroup.addParticle(new TransferrableTokensParticle(
			universeAddress, HUNDRED, UInt256.ONE, consumable.getTokDefRef(),
			currentPlanckTime(),
			consumable.getTokenPermissions()
		), Spin.UP);
		secondAtom.addParticleGroup(secondGroup.build());
		addTemporalVertex(secondAtom); // Can't store atom without vertex from this node
		secondAtom.sign(identity);

		Pair<CMAtom, UInt384> result1 = Modules.get(ValidationHandler.class).validate(secondAtom);
		Assertions.assertThatThrownBy(() -> Modules.get(ValidationHandler.class).stateCheck(result1.getFirst()))
			.isInstanceOf(ParticleConflictException.class);
	}

}