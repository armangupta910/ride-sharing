
<h1>RideSharing App</h1>
<p>A ride-sharing Android application built using Kotlin, Firebase Realtime Database, and OpenRouteService API. This app displays user and driver locations on a map, calculates distances, and provides routing information.</p>

<h2>Table of Contents</h2>
<ul>
    <li><a href="#features">Features</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#prerequisites">Prerequisites</a></li>
    <li><a href="#installation">Installation</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#screenshots">Screenshots</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
</ul>

<h2 id="features">Features</h2>
<ul>
    <li>Fetch user location and display it on a map.</li>
    <li>Fetch nearby driver locations and display them on the map.</li>
    <li>Calculate the shortest route from the user to the closest driver.</li>
    <li>Calculate the route from the driver to the destination.</li>
    <li>Display user and driver on the map.</li>
    <li>Fetch driver data from Firebase Realtime Database.</li>
</ul>

<h2 id="getting-started">Getting Started</h2>
<p>These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.</p>

<h3 id="prerequisites">Prerequisites</h3>
<ul>
    <li>Android Studio</li>
    <li>Firebase Account</li>
    <li>OpenRouteService API Key</li>
</ul>

<h3 id="installation">Installation</h3>
<ol>
    <li><strong>Clone the repo:</strong>
        <pre><code>git clone https://github.com/yourusername/ridesharing-app.git
cd ridesharing-app</code></pre>
    </li>
    <li><strong>Open the project in Android Studio:</strong>
        <p>Open Android Studio, click on <code>File -> Open</code> and navigate to the directory where you cloned the repository.</p>
    </li>
    <li><strong>Add your OpenRouteService API Key:</strong>
        <p>Replace the <code>apiKey</code> in <code>MainActivity.kt</code> with your OpenRouteService API key:</p>
        <pre><code>private val apiKey = "YOUR_API_KEY"</code></pre>
    </li>
    <li><strong>Set up Firebase:</strong>
        <ul>
            <li>Go to the <a href="https://console.firebase.google.com/">Firebase Console</a>.</li>
            <li>Create a new project or use an existing one.</li>
            <li>Add an Android app to your Firebase project.</li>
            <li>Follow the instructions to download the <code>google-services.json</code> file.</li>
            <li>Place the <code>google-services.json</code> file in the <code>app</code> directory of your project.</li>
        </ul>
    </li>
    <li><strong>Sync the project with Gradle files:</strong>
        <p>Click on <code>File -> Sync Project with Gradle Files</code> in Android Studio to ensure all dependencies are downloaded.</p>
    </li>
</ol>

<h3 id="usage">Usage</h3>
<ol>
    <li><strong>Run the app:</strong>
        <p>Connect your Android device or start an Android emulator. Click the Run button in Android Studio to build and install the app on your device.</p>
    </li>
    <li><strong>Grant Location Permissions:</strong>
        <p>The app requires location permissions to fetch and display your current location. Grant the necessary permissions when prompted.</p>
    </li>
    <li><strong>Interact with the map:</strong>
        <ul>
            <li>The app will display your current location on the map.</li>
            <li>Nearby drivers will be fetched and displayed.</li>
            <li>The shortest route to the closest driver will be calculated and displayed.</li>
            <li>The route from the driver to the destination will also be shown.</li>
        </ul>
    </li>
</ol>

<h2 id="screenshots">Screenshots</h2>
<p><em>Include screenshots of your app here</em></p>

<h2 id="built-with">Built With</h2>
<ul>
    <li><a href="https://kotlinlang.org/">Kotlin</a> - Programming language used</li>
    <li><a href="https://firebase.google.com/products/realtime-database">Firebase Realtime Database</a> - Realtime database</li>
    <li><a href="https://openrouteservice.org/sign-up/">OpenRouteService API</a> - Routing and distance calculation</li>
    <li><a href="https://github.com/osmdroid/osmdroid">osmdroid</a> - Map rendering</li>
</ul>

<h2 id="contributing">Contributing</h2>
<p>Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are <strong>greatly appreciated</strong>.</p>
<ol>
    <li>Fork the Project</li>
    <li>Create your Feature Branch (<code>git checkout -b feature/YourFeature</code>)</li>
    <li>Commit your Changes (<code>git commit -m 'Add some YourFeature'</code>)</li>
    <li>Push to the Branch (<code>git push origin feature/YourFeature</code>)</li>
    <li>Open a Pull Request</li>
</ol>

<h2 id="license">License</h2>
<p>Distributed under the MIT License. See <code>LICENSE</code> for more information.</p>

<h2 id="acknowledgements">Acknowledgements</h2>
<ul>
    <li><a href="https://openrouteservice.org/">OpenRouteService</a></li>
    <li><a href="https://github.com/osmdroid/osmdroid">osmdroid</a></li>
    <li><a href="https://firebase.google.com/">Firebase</a></li>
</ul>

</body>
</html>
