package XmlMonitor.HibernateEntities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "xml_files_entries", schema = "org", catalog = "")
public class XmlFilesEntriesEntity {
    private int id;
    private String filename;
    private Integer entryId;
    private String entryContent;
    private Timestamp entryCreationDate;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    //@GenericGenerator(name="generator", strategy="increment")
    //@GeneratedValue(generator="generator")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "filename", nullable = false, length = 100)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Basic
    @Column(name = "entry_id", nullable = true)
    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    @Basic
    @Column(name = "entry_content", nullable = true, length = 1000)
    public String getEntryContent() {
        return entryContent;
    }

    public void setEntryContent(String entryContent) {
        this.entryContent = entryContent;
    }

    @Basic
    @Column(name = "entry_creation_date", nullable = true)
    public Timestamp getEntryCreationDate() {
        return entryCreationDate;
    }

    public void setEntryCreationDate(Timestamp entryCreationDate) {
        this.entryCreationDate = entryCreationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XmlFilesEntriesEntity that = (XmlFilesEntriesEntity) o;

        if (id != that.id) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
        if (entryId != null ? !entryId.equals(that.entryId) : that.entryId != null) return false;
        if (entryContent != null ? !entryContent.equals(that.entryContent) : that.entryContent != null) return false;
        if (entryCreationDate != null ? !entryCreationDate.equals(that.entryCreationDate) : that.entryCreationDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (entryId != null ? entryId.hashCode() : 0);
        result = 31 * result + (entryContent != null ? entryContent.hashCode() : 0);
        result = 31 * result + (entryCreationDate != null ? entryCreationDate.hashCode() : 0);
        return result;
    }
}
