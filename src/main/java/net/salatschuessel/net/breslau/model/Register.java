package net.salatschuessel.net.breslau.model;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Register {
	private Integer year;
	private Integer volume;
	private Integer numberFrom;
	private Integer numberTo;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private boolean isMainRegister;
	private boolean isMissing;
	private boolean isOnline;
	private URL url;
	private String note;

	public Integer getYear() {
		return this.year;
	}

	public void setYear(final Integer year) {
		this.year = year;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public void setVolume(final Integer volume) {
		this.volume = volume;
	}

	public Integer getNumberFrom() {
		return this.numberFrom;
	}

	public void setNumberFrom(final Integer numberFrom) {
		this.numberFrom = numberFrom;
	}

	public Integer getNumberTo() {
		return this.numberTo;
	}

	public void setNumberTo(final Integer numberTo) {
		this.numberTo = numberTo;
	}

	public LocalDate getDateFrom() {
		return this.dateFrom;
	}

	public void setDateFrom(final LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateTo() {
		return this.dateTo;
	}

	public void setDateTo(final LocalDate dateTo) {
		this.dateTo = dateTo;
	}

	public boolean isMainRegister() {
		return this.isMainRegister;
	}

	public void setMainRegister(final boolean isMainRegister) {
		this.isMainRegister = isMainRegister;
	}

	public boolean isMissing() {
		return this.isMissing;
	}

	public void setMissing(final boolean isMissing) {
		this.isMissing = isMissing;
	}

	public boolean isOnline() {
		return this.isOnline;
	}

	public void setOnline(final boolean isOnline) {
		this.isOnline = isOnline;
	}

	public URL getUrl() {
		return this.url;
	}

	public void setUrl(final URL url) {
		this.url = url;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.isMainRegister, this.volume, this.year);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final Register other = (Register) obj;
		return this.isMainRegister == other.isMainRegister && Objects.equals(this.volume, other.volume)
				&& Objects.equals(this.year, other.year);
	}

	@Override
	public String toString() {
		return "Register [year=" + this.year + ", volume=" + this.volume + ", numberFrom=" + this.numberFrom
				+ ", numberTo=" + this.numberTo
				+ ", dateFrom=" + this.dateFrom + ", dateTo=" + this.dateTo + ", isMainRegister=" + this.isMainRegister
				+ ", isMissing=" + this.isMissing + ", isOnline=" + this.isOnline + ", url=" + this.url + ", note="
				+ this.note + "]";
	}

	@JsonIgnore
	public Archive getArchiv() {
		if (this.getUrl() == null)
			return null;
		final String urlStr = this.getUrl().toString();
		if (urlStr.contains("ancestry")) {
			return Archive.ANCESTRY;
		} else if (urlStr.contains("szukajwarchiwach")) {
			return Archive.STAATSARCHIV_BRESLAU;
		} else if (urlStr.contains("landesarchiv-berlin")) {
			return Archive.LANDESARCHIV_BERLIN;
		} else if (urlStr.contains("berlin")) {
			return Archive.STANDESAMT_1_BERLIN;
		} else if (urlStr.contains("bip.um.wroc.pl")) {
			return Archive.STANDESAMT_BRESLAU;
		}
		return null;
	}
}
