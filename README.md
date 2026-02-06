# Cyclone Sounds
> Note: Active development on this project has **stopped**.  
> The repository is kept for reference and as a record of our COM S 3090 work.

## Description
Cyclone Sounds is a music listening and review application built for **COM S 3090 – Software Development Practices** at **Iowa State University**.  
The project lets users browse songs and albums, write reviews, see friends’ activity, and listen to music through an integrated Android client and Spring Boot backend.

## Project Structure

Backend/  
- src/main/java/cycloneSounds/...  
- src/test/...  
- pom.xml  

Frontend/  
- AndroidExample/  
  - app/src/main/java/com/example/androidexample/...  
  - app/src/main/res/...  
  - app/build.gradle  

Documents/  
- Design documents, reports, and other course materials  

- Backend: REST API, security configuration, and domain logic (albums, reviews, friends, Spotify, websockets, etc.).  
- Frontend: Native Android Activities for login, home, music player, playlists, reviews, chat, and Spotify playback.

## Installation

### Requirements
- Java 17 or later  
- Maven  
- Android Studio (latest stable)  
- Android SDK and an emulator or physical Android device  
- (Optional) Spotify developer credentials if you want Spotify features enabled

### Backend Setup (Spring Boot)

1. Open the `Backend/` directory in your IDE or terminal.  
2. Configure the database and other settings in `Backend/src/main/resources/application.properties` (or `application.yml`).  
3. From `Backend/`, run:

   mvn spring-boot:run

4. The backend will run on `http://localhost:8080` unless configured otherwise.

### Android App Setup

1. Open Android Studio and choose “Open an Existing Project”, selecting `Frontend/AndroidExample`.  
2. Let Gradle sync and download dependencies.  
3. Set the backend base URL in your networking code (for example, in `VolleySingleton` or a constants file) to point to the backend:  
   - For Android emulator: `http://10.0.2.2:8080`  
   - For physical device: `http://<your-machine-ip>:8080`  
4. Choose an emulator or device and click Run.

## Usage

A typical user flow:

1. Launch Cyclone Sounds and create an account or log in.  
2. Browse jams, songs, or albums from the home screen.  
3. Tap an item to see details and existing reviews.  
4. Add your own review and rating.  
5. View friends, social history, and playlists to explore what others are listening to.  
6. Use the built-in player or Spotify integration to listen.

Example backend API endpoints (adjust names to match your controllers):

- GET `/api/songs`  
- GET `/api/songs/{id}`  
- POST `/api/reviews`  
- GET `/api/users/{id}/friends`

## Support
If you run into problems:

- Open an issue in the project’s issue tracker.  
- Contact the team using your Iowa State email addresses (martink5@iastate.edu)

You can also document any known issues or troubleshooting tips here (for example, common Gradle errors or emulator networking issues).

## Roadmap
Potential future improvements:

- Push notifications for new reviews and friend activity.  
- Recommendation engine based on listening and review history.  
- Enhanced Spotify/streaming integration and better queue management.  
- Additional themes (for example, dark mode) and accessibility improvements.  

## Contributing
This repository was created for a course project, but contributions and refinements are welcome.

1. Fork the repository.  
2. Create a feature branch from `main`.  
3. Make changes in `Backend/` and/or `Frontend/AndroidExample/`.  
4. Ensure the app builds and passes tests.  
5. Open a merge request with a clear description of your changes.

### Running Tests

Backend (JUnit):

- cd Backend  
- mvn test  

Android (unit/instrumentation tests):

- cd Frontend/AndroidExample  
- ./gradlew test  
- ./gradlew connectedAndroidTest  

## Authors and Acknowledgment
- Martin Kochadampally & Mark Seward – Backend services, database and CI/CD
- Alexander Dwitty & Jackson Danby – Android UI and features   

Special thanks to the COM S 3090 instructors and TAs for guidance and feedback.
