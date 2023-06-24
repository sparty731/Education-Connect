// STEP 1 - Imports
import scala.util.{Try, Success, Failure}
import scala.collection.mutable.ArrayBuffer
import java.io.File

// STEP 2 - Database Connection
object DBConnect {
  val hostname = "localhost"
  val port = 5433
  val database = "education_connect"
  val username = "ecuser"
  val password = "pas$word1"
  
  // Connect to the database
  def connect(): Try[java.sql.Connection] = Try {
    Class.forName("org.postgresql.Driver")
    java.sql.DriverManager.getConnection(
      s"jdbc:postgresql://$hostname:$port/$database",
      username,
      password
    )
  }
}

// STEP 3 - Data Model Model
case class Student(name: String, age: Int, grade: Int)

// STEP 4 - Program Logic
object EducationConnect {
  // Retrieve students from the database
  def getStudents(): Try[ArrayBuffer[Student]] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    val rs = stmt.executeQuery("SELECT * FROM students")
    
    // Create the student Buffer
    val students = ArrayBuffer[Student]()
    
    // Iterate over the returned records and add them to the buffer
    while(rs.next()) {
      val name = rs.getString("name")
      val age = rs.getInt("age")
      val grade = rs.getInt("grade")
      
      students.append(Student(name, age, grade))
    }
    
    // Close the statement and connection
    stmt.close()
    conn.close()
    
    students
  }
  
  // Add a student to the database
  def addStudent(student: Student): Try[Unit] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    stmt.executeUpdate(s"INSERT INTO students (name, age, grade) " + 
      s"VALUES ('${student.name}', ${student.age}, ${student.grade})")
    
    // Close the statement and connection
    stmt.close()
    conn.close()
  }
  
  // Remove a student from the database
  def removeStudent(name: String): Try[Unit] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    stmt.executeUpdate(s"DELETE FROM students WHERE name = '$name'")
    
    // Close the statement and connection
    stmt.close()
    conn.close()
  }
  
  // Update a student's information
  def updateStudent(student: Student): Try[Unit] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    stmt.executeUpdate(s"UPDATE students SET " + 
      s"age = ${student.age}, grade = ${student.grade} " + 
      s"WHERE name = '${student.name}'")
    
    // Close the statement and connection
    stmt.close()
    conn.close()
  }
  
  // Load student data from a CSV file
  def loadStudentsFromCSV(file: File): Try[Unit] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    
    // Read the CSV file
    import scala.io.Source
    val lines = Source.fromFile(file).getLines.toArray
    
    // Iterate over the lines, extract the data and insert into the database
    lines.foreach{ line =>
      val fields = line.split(",")
      val name = fields(0)
      val age = fields(1).toInt
      val grade = fields(2).toInt
      
      stmt.executeUpdate(s"INSERT INTO students (name, age, grade) " + 
        s"VALUES ('$name', $age, $grade)")
    }
    
    // Close the statement and connection
    stmt.close()
    conn.close()
  }
  
  // Export student data to a CSV file
  def exportStudentsToCSV(file: File): Try[Unit] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    val rs = stmt.executeQuery("SELECT * FROM students ORDER BY grade")
    
    // Retrieve the student records from the database
    val students = ArrayBuffer[String]()
    while(rs.next()) {
      val name = rs.getString("name")
      val age = rs.getInt("age")
      val grade = rs.getInt("grade")
      
      students.append(s"$name,$age,$grade")
    }
    
    // Write the student records to the CSV file
    import java.io._
    val pw = new PrintWriter(file)
    students.foreach { student =>
      pw.write(s"$student\n")
    }
    pw.close
    
    // Close the statement and connection
    stmt.close()
    conn.close()
  }
  
  // Load student data from an XML file
  def loadStudentsFromXML(file: File): Try[Unit] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    
    // Read the XML file
    import scala.xml.XML
    val xmlData = XML.loadFile(file)
    
    // Iterate over the students, extract the data and insert into the database
    (xmlData \ "student").foreach { s =>
      val name = (s \ "name").text
      val age = (s \ "age").text.toInt
      val grade = (s \ "grade").text.toInt
      
      stmt.executeUpdate(s"INSERT INTO students (name, age, grade) " + 
        s"VALUES ('$name', $age, $grade)")
    }
    
    // Close the statement and connection
    stmt.close()
    conn.close()
  }
  
  // Export student data to an XML file
  def exportStudentsToXML(file: File): Try[Unit] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    val rs = stmt.executeQuery("SELECT * FROM students ORDER BY grade")
    
    // Retrieve the student records from the database
    val students = ArrayBuffer[Map[String, Any]]()
    while(rs.next()) {
      val name = rs.getString("name")
      val age = rs.getInt("age")
      val grade = rs.getInt("grade")
      
      students.append(Map("name" -> name, "age" -> age, "grade" -> grade))
    }
    
    // Convert the students to XML
    import scala.xml.XML
    val studentsXML = students.map { student =>
      val name = student("name").asInstanceOf[String]
      val age = student("age").asInstanceOf[Int]
      val grade = student("grade").asInstanceOf[Int]
      
      <student>
        <name>{name}</name>
        <age>{age}</age>
        <grade>{grade}</grade>
      </student>
    }
    
    // Write the XML to the file
    import java.io._
    val pw = new PrintWriter(file)
    pw.write(XML.toString(<students>{studentsXML}</students>))
    pw.close
    
    // Close the statement and connection
    stmt.close()
    conn.close()
  }
  
  // Get the average grade of all students
  def getAverageGrade(): Try[Double] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    val rs = stmt.executeQuery("SELECT AVG(grade) FROM students")
    
    // Retrieve the average grade
    rs.next()
    val avgGrade = rs.getDouble(1)
    
    // Close the statement and connection
    stmt.close()
    conn.close()
    
    avgGrade
  }
  
  // Get the student with the highest grade
  def getTopStudent(): Try[Student] = Try {
    // Connect to the database
    val conn = DBConnect.connect().get
    val stmt = conn.createStatement
    val rs = stmt.executeQuery("SELECT * FROM students ORDER BY grade DESC LIMIT 1")
    
    // Retrieve the top student
    rs.next()
    val name = rs.getString("name")
    val age = rs.getInt("age")
    val grade = rs.getInt("grade")
    
    // Close the statement and connection
    stmt.close()
    conn.close()
    
    Student(name, age, grade)
  }
}