import HoverVideoGuide from './HoverVideoGuide';

// Array of objects representing the steps to sign up for a tournament
const signUpSteps = [
    {
        title: "Go to Solo Queue",
        description: "Ensure that you allow Quagmire to access your location in Settings.",
        videoUrl: "/videos/gotoQueue.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Join the queue",
        description: "Wait for a match! Good things take time, be patient...",
        videoUrl: "/videos/joinQ.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Wrestle",
        description: "3, 2, 1, begin!",
        videoUrl: "/videos/arm wrestle.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Record your match results",
        description: "Honesty is the best policy.",
        videoUrl: "/videos/iwon.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Forfeit",
        description: "In the event of a no show after 10 minutes, please do not continue waiting.",
        videoUrl: "/videos/forfeit.mp4" // URL of the video demonstrating this step
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