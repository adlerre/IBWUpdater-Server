/*
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 3
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
package ibw.updater.datamodel.adapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import ibw.updater.datamodel.User;
import ibw.updater.datamodel.adapter.UserAdapter.BasicUser;
import ibw.updater.persistency.UserManager;

public class UserAdapter extends XmlAdapter<BasicUser, User> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public User unmarshal(BasicUser v) throws Exception {
		return v.getId() != 0 ? UserManager.get(v.getId()) : UserManager.get(v.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public BasicUser marshal(User v) throws Exception {
		return new BasicUser(v.getId(), v.getName());
	}

	@XmlRootElement(name = "user")
	static class BasicUser {
		private int id;

		private String name;

		BasicUser() {
		}

		/**
		 * @param id
		 * @param name
		 * @param description
		 */
		public BasicUser(int id, String name) {
			this.id = id;
			this.name = name;
		}

		/**
		 * @return the id
		 */
		@XmlAttribute(name = "id")
		public int getId() {
			return id;
		}

		/**
		 * @param id
		 *            the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * @return the name
		 */
		@XmlAttribute(name = "name")
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

	}

}
