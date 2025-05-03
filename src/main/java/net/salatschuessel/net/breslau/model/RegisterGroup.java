package net.salatschuessel.net.breslau.model;

import java.util.Objects;

public class RegisterGroup {
	private Register mainRegister;
	private Register secondaryRegister;

	public Register getMainRegister() {
		return this.mainRegister;
	}

	public void setMainRegister(final Register mainRegister) {
		this.mainRegister = mainRegister;
	}

	public Register getSecondaryRegister() {
		return this.secondaryRegister;
	}

	public void setSecondaryRegister(final Register secondaryRegister) {
		this.secondaryRegister = secondaryRegister;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.mainRegister, this.secondaryRegister);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final RegisterGroup other = (RegisterGroup) obj;
		return Objects.equals(this.mainRegister, other.mainRegister)
				&& Objects.equals(this.secondaryRegister, other.secondaryRegister);
	}

	@Override
	public String toString() {
		return "RegisterGroup [mainRegister=" + this.mainRegister + ", secondaryRegister=" + this.secondaryRegister
				+ "]";
	}

}
