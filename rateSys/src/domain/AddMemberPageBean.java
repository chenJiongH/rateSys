package domain;

import java.util.List;

public class AddMemberPageBean {
	List<PSPgroup> group;
	List<PSPGmember> member;
	List<PSpecialists> people;
	List<Project> allProject;
	public List<Project> getAllProject() {
		return allProject;
	}
	public void setAllProject(List<Project> allProject) {
		this.allProject = allProject;
	}
	public List<PSPgroup> getGroup() {
		return group;
	}
	public void setGroup(List<PSPgroup> group) {
		this.group = group;
	}
	public List<PSPGmember> getMember() {
		return member;
	}
	public void setMember(List<PSPGmember> member) {
		this.member = member;
	}
	public List<PSpecialists> getPeople() {
		return people;
	}
	public void setPeople(List<PSpecialists> people) {
		this.people = people;
	}
	@Override
	public String toString() {
		return "AddMemberPageBean [group=" + group + ", member=" + member + ", people=" + people + ", allProject="
				+ allProject + "]";
	}

}
