import HoverVideoGuide from './HoverVideoGuide';

// Array of objects representing the steps to sign up for a tournament
const signUpSteps = [
    {
        title: "Go to SoloQueue",
        description: "Ensure that you allow Quagmire to access your location in Settings.",
        videoUrl: "/videos/buffswim.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Join the queue",
        description: "Wait for a match! Good things take time, be patient...",
        videoUrl: "/videos/buffsuit.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Pop off",
        description: "3, 2, 1, begin!",
        videoUrl: "/videos/sweatycode.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Record your match results",
        description: "Honesty is the best policy.",
        videoUrl: "/videos/jjk.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Forfeit",
        description: "In the event of a no show after 10 minutes, please do not continue waiting.",
        videoUrl: "/videos/idwin.mp4" // URL of the video demonstrating this step
    }
];

/**
 * HeroVideos component
 * This component renders a section containing a guide for signing up for a tournament.
 * It uses the HoverVideoGuide component to display the steps with associated videos.
 */
export default function HeroVideos() {
    return (
        <div className='w-full py-12'>
            {/* Render the HoverVideoGuide component with the signUpSteps data */}
            <HoverVideoGuide steps={signUpSteps} />
        </div>
    );
}