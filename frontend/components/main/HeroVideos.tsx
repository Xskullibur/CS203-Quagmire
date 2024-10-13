import HoverVideoGuide from './HoverVideoGuide';

// Array of objects representing the steps to sign up for a tournament
const signUpSteps = [
    {
        title: "Find a Tournament",
        description: "Search for upcoming arm wrestling tournaments in your area or online.",
        videoUrl: "/videos/buffswim.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Check Eligibility",
        description: "Review the tournament rules and ensure you meet all requirements.",
        videoUrl: "/videos/buffsuit.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Register Online",
        description: "Complete the online registration form and pay the entry fee.",
        videoUrl: "/videos/sweatycode.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Prepare for Weigh-In",
        description: "Get ready for the official weigh-in before the tournament.",
        videoUrl: "/videos/jjk.mp4" // URL of the video demonstrating this step
    },
    {
        title: "Attend the Event",
        description: "Arrive at the venue on time and participate in the tournament.",
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