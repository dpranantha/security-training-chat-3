package org.certifiedsecure.chat.persistence.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Channel {
	@Id
	@GeneratedValue
	private Long id;

	public Long getId() {
		return id;
	}

	@ManyToOne
	private Realm realm;

	public Realm getRealm() {
		return realm;
	}

	public String name;

	public String getName() {
		return name;
	}

	@OneToMany(mappedBy = "channel")
	@OrderBy("id ASC")
	private Set<Message> messages = new HashSet<>();

	public Set<Message> getMessages() {
		return messages;
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "channel_user", joinColumns = @JoinColumn(name = "channel_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
	private Set<User> users;

	public Set<User> getUsers() {
		return users;
	}

	public void addUser(User user) {
		if (users == null) {
			users = new HashSet<User>();
		}
		this.users.add(user);
	}

	public Channel(Realm realm, String name) {
		this.realm = realm;
		this.name = name;
	}

	public Channel() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((realm == null) ? 0 : realm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Channel other = (Channel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (realm == null) {
			if (other.realm != null)
				return false;
		} else if (!realm.equals(other.realm))
			return false;
		return true;
	}
}
