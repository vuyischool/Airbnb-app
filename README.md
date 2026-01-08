# Airbnb Property Rental System

A comprehensive JavaFX-based property rental system similar to Airbnb, built with Java and file-based data storage.

## 🎯 Project Overview

This application is a complete property rental management system that supports three user roles:
- **Guest**: Search and book properties, leave reviews, send messages
- **Host**: Manage properties, view bookings, track earnings, see reviews
- **Admin**: Comprehensive system overview with statistics and management capabilities

## 🛠️ Technology Stack

- **Language**: Java
- **GUI Framework**: JavaFX
- **IDE**: NetBeans (project structure)
- **Data Storage**: File-based (text files)
- **Security**: SHA-256 password hashing (Java Security API)
- **Session Management**: Static session simulation

## 📋 Features

### User Authentication & Security
- User registration with role selection (Guest/Host)
- Secure login with SHA-256 password hashing
- Session persistence between application runs
- Role-based access control

### Property Management (Host Features)
- Add, update, and delete property listings
- Set availability dates and pricing
- Upload property images (file path storage)
- Manage multiple properties per host

### Search & Booking System (Guest Features)
- Search listings by location, price range, and rating
- Book available properties with date selection
- Automatic availability checking
- Booking confirmation and history

### Reviews & Ratings System
- Leave reviews with 1-5 star ratings
- **Automatic average rating calculation** (updates in real-time)
- View reviews for properties
- Display average ratings on property listings
- Rating updates immediately when new review is added

### Messaging System
- Text-based messaging between users
- Guest ↔ Host communication
- Message history persistence
- Conversation view

### Admin Dashboard
- **Users Tab**: View all users with ID, Username, Email, Type, Registration Date
- **Properties Tab**: Complete listing details with owner information
- **Bookings Tab**: Complete transaction history with amounts, dates, status
- **Statistics Tab**: System metrics (total users, listings, bookings, revenue, average ratings)

### Host Dashboard
- **My Properties**: All properties owned by current host
- **Bookings**: Who booked, dates, status, total price for host's properties
- **Reviews**: All reviews with ratings for owned properties
- **Earnings Summary**: Total and pending earnings from bookings

## 🏗️ Project Structure

```
AirbnbPropertyRentalSystem/
├── src/
│   └── com/
│       └── airbnb/
│           ├── Main.java
│           ├── controller/
│           │   ├── AuthController.java
│           │   ├── DashboardController.java
│           │   ├── PropertyController.java
│           │   ├── PropertyDetailController.java
│           │   ├── BookingController.java
│           │   ├── ReviewController.java
│           │   ├── MessageController.java
│           │   ├── AdminDashboardController.java
│           │   └── HostDashboardController.java
│           ├── models/
│           │   ├── User.java
│           │   ├── Property.java
│           │   ├── Booking.java
│           │   ├── Review.java
│           │   └── Message.java
│           ├── services/
│           │   ├── FileService.java
│           │   ├── AuthService.java
│           │   ├── PropertyService.java
│           │   ├── BookingService.java
│           │   ├── ReviewService.java
│           │   ├── MessageService.java
│           │   ├── AdminService.java
│           │   └── HostService.java
│           ├── utils/
│           │   ├── SecurityUtil.java
│           │   ├── ValidationUtil.java
│           │   ├── SessionManager.java
│           │   └── SceneManager.java
│           └── views/ (FXML files)
│               ├── Login.fxml
│               ├── Register.fxml
│               ├── MainDashboard.fxml
│               ├── PropertyList.fxml
│               ├── PropertyDetail.fxml
│               ├── Booking.fxml
│               ├── Reviews.fxml
│               ├── Messages.fxml
│               ├── AdminDashboard.fxml
│               └── HostDashboard.fxml
├── data/ (created automatically)
│   ├── users.txt
│   ├── listings.txt
│   ├── bookings.txt
│   ├── reviews.txt
│   └── messages.txt
└── README.md
```

## 🚀 Setup Instructions

### Prerequisites
- **JDK 11 or higher** (JavaFX is included in JDK 11+)
- **NetBeans IDE** (recommended) or any Java IDE
- JavaFX SDK (if using JDK 8 or earlier)

### Running the Application

#### Option 1: Using NetBeans
1. Open NetBeans IDE
2. File → Open Project → Select the `Airbnbsystem` folder
3. Right-click on the project → Run

#### Option 2: Using Command Line
1. Navigate to the project directory
2. Compile the project:
   ```bash
   javac -cp "path/to/javafx/lib/*" src/com/airbnb/**/*.java
   ```
3. Run the application:
   ```bash
   java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp src com.airbnb.Main
   ```

#### Option 3: Using JAR File
1. Build the JAR file (instructions in NetBeans: Right-click project → Clean and Build)
2. Run the JAR:
   ```bash
   java -jar airbnb-system.jar
   ```

### First-Time Setup

1. **Create an Admin User** (optional):
   - Register a new user and manually edit `data/users.txt` to change the user type to "ADMIN"
   - Or use the registration form and modify the file directly

2. **Data Directory**:
   - The `data/` directory is created automatically on first run
   - All data files are initialized automatically

## 📊 Data File Formats

### users.txt
```
id|username|email|passwordHash|userType|registrationDate
```

### listings.txt
```
id|title|description|location|price|ownerId|averageRating|imagePath|availableDates
```

### bookings.txt
```
id|propertyId|guestId|checkIn|checkOut|totalPrice|status
```

### reviews.txt
```
id|propertyId|userId|rating|comment|date
```

### messages.txt
```
id|senderId|receiverId|content|timestamp|isRead
```

## 🔑 Key Features Implementation

### Automatic Rating Calculation
The system automatically calculates and updates property ratings when reviews are added:

```java
// In PropertyService.recalculateRating()
Property property = getPropertyById(propertyId);
List<Review> reviews = ReviewService.getReviewsByPropertyId(propertyId);
property.setReviews(reviews);
property.calculateAverageRating(); // Uses Java Streams
updateProperty(property);
```

### Java Streams Usage
The application extensively uses Java Streams for:
- **Filtering**: Property search, booking filtering
- **Calculations**: Average ratings, earnings, statistics
- **Data Processing**: User lists, message formatting

Example:
```java
List<Property> filtered = properties.stream()
    .filter(p -> p.getLocation().toLowerCase().contains(location))
    .filter(p -> p.getPrice() <= maxPrice)
    .filter(p -> p.getAverageRating() >= minRating)
    .collect(Collectors.toList());
```

### Lambda Expressions
Lambdas are used throughout for:
- Event handlers in controllers
- Stream operations
- Data transformations

Example:
```java
searchButton.setOnAction(e -> handleSearch());
```

## 🧪 Testing the Application

1. **Register Users**:
   - Register as a Guest
   - Register as a Host
   - (Optional) Create an Admin user manually

2. **Host Functions**:
   - Login as Host
   - Add properties
   - View Host Dashboard
   - Check bookings and earnings

3. **Guest Functions**:
   - Login as Guest
   - Search properties
   - Book a property
   - Leave reviews (watch rating update automatically)
   - Send messages

4. **Admin Functions**:
   - Login as Admin
   - View all users, properties, bookings
   - Check system statistics

## 📝 Notes

- All data is stored in text files in the `data/` directory
- Passwords are hashed using SHA-256
- Ratings update automatically when reviews are added
- The system uses Java Streams extensively for data processing
- Session is simulated using a static `currentUser` object

## 🐛 Troubleshooting

### Application won't start
- Ensure JavaFX is properly configured
- Check that all FXML files are in the correct package structure
- Verify the `data/` directory is writable

### Can't see properties
- Ensure you've added properties as a Host
- Check that `data/listings.txt` exists and has data

### Rating not updating
- Ratings update automatically when reviews are added
- Check `data/reviews.txt` and `data/listings.txt` files

## 📄 License

This project is created for educational purposes.

## 👤 Author

Vuyisile Nqono

---

**Built with JavaFX | SHA-256 Security | File-based Storage**